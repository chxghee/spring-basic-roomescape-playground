package roomescape.waiting.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.AuthException;
import roomescape.auth.LoginMember;
import roomescape.exception.ApplicationException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.exception.ReservationException;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;
import roomescape.time.domain.Time;
import roomescape.time.domain.TimeRepository;
import roomescape.waiting.application.command.WaitingCommand;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.domain.WaitingOrderCounter;
import roomescape.waiting.domain.repository.WaitingOrderCounterRepository;
import roomescape.waiting.domain.repository.WaitingRepository;
import roomescape.waiting.exception.WaitingException;
import roomescape.waiting.presentation.response.WaitingResponse;

@Service
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final WaitingOrderCounterRepository waitingOrderCounterRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final ThemeRepository themeRepository;
    private final TimeRepository timeRepository;
    private final WaitingSaveService waitingSaveService;

    public WaitingService(WaitingRepository waitingRepository, WaitingOrderCounterRepository waitingOrderCounterRepository,  ReservationRepository reservationRepository,
                          MemberRepository memberRepository, ThemeRepository themeRepository, TimeRepository timeRepository, WaitingSaveService waitingSaveService) {
        this.waitingRepository = waitingRepository;
        this.waitingOrderCounterRepository = waitingOrderCounterRepository;
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.themeRepository = themeRepository;
        this.timeRepository = timeRepository;
        this.waitingSaveService = waitingSaveService;
    }

    public WaitingResponse save(WaitingCommand command) {
        Time time = timeRepository.getTimeById(command.time());
        Theme theme = themeRepository.getThemeById(command.theme());
        Member member = memberRepository.getMemberById(command.memberId());

        validateDuplicateRequest(command.date(), member, time, theme);
        Waiting newWaiting = waitingSaveService.createWaiting(command, theme, time, member);
        return WaitingResponse.from(newWaiting);
    }

    private void validateDuplicateRequest(String date, Member member, Time time, Theme theme) {
        if (waitingRepository.existsByMemberAndDateAndTimeAndTheme(member, date, time, theme)) {
            throw new ApplicationException(WaitingException.DUPLICATE_WAITING_REQUEST);
        }

        if (reservationRepository.existsByMemberAndDateAndTimeAndTheme(member, date, time, theme)) {
            throw new ApplicationException(ReservationException.DUPLICATE_RESERVATION_REQUEST);
        }
    }

    @Transactional
    public void delete(LoginMember loginMember, Long waitingId) {
        Waiting waiting = waitingRepository.getWaitingById(waitingId);

        if (!waiting.belongsTo(loginMember.id())) {
            throw new ApplicationException(AuthException.FORBIDDEN_ACCESS);
        }

        // 1. 카운터 테이블에서 해당 예약의 row를 찾아 비관적 락
        WaitingOrderCounter counter = waitingOrderCounterRepository.findByThemeIdAndDateAndTimeIdWithLock(
                        waiting.getTheme().getId(), waiting.getDate(), waiting.getTime().getId())
                .orElseThrow(() -> new ApplicationException(WaitingException.WAITING_ORDER_COUNTER_NOTFOUND));

        // 2. 벌크 업데이트 쿼리 -> 준영속 상태가 됨
        waitingRepository.decrementOrder(waiting.getTheme(), waiting.getDate(), waiting.getTime(), waiting.getOrder());

        waitingRepository.delete(waiting);
        counter.decreaseOrder();
        waitingOrderCounterRepository.save(counter);
    }
}
