package roomescape.waiting.application;

import org.springframework.dao.DataIntegrityViolationException;
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
import roomescape.waiting.domain.WaitingOrderCounterRepository;
import roomescape.waiting.domain.WaitingRepository;
import roomescape.waiting.exception.WaitingException;
import roomescape.waiting.presentation.response.WaitingResponse;

@Service
@Transactional(readOnly = true)
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final WaitingOrderCounterRepository waitingOrderCounterRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final ThemeRepository themeRepository;
    private final TimeRepository timeRepository;

    public WaitingService(WaitingRepository waitingRepository, WaitingOrderCounterRepository waitingOrderCounterRepository,  ReservationRepository reservationRepository,
                          MemberRepository memberRepository, ThemeRepository themeRepository, TimeRepository timeRepository) {
        this.waitingRepository = waitingRepository;
        this.waitingOrderCounterRepository = waitingOrderCounterRepository;
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.themeRepository = themeRepository;
        this.timeRepository = timeRepository;
    }

    public WaitingResponse save(WaitingCommand command) {
        Time time = timeRepository.getTimeById(command.time());
        Theme theme = themeRepository.getThemeById(command.theme());
        Member member = memberRepository.getMemberById(command.memberId());

        validateDuplicateRequest(command.date(), member, time, theme);

        int maxAttempts = 2;
        int attempts = 0;
        // 카운터 생성 충돌시 재시도 로직
        while (attempts < maxAttempts) {
            attempts++;
            try {
                Waiting newWaiting = createWaitingWithLock(command, theme, time, member);
                return WaitingResponse.from(newWaiting);
            } catch (DataIntegrityViolationException e) {
                if (attempts >= maxAttempts) throw new ApplicationException(WaitingException.RETRY_WAITING_ORDER_COUNTER_FAILED);
            }
        }
        throw new ApplicationException(WaitingException.RETRY_WAITING_ORDER_COUNTER_FAILED);
    }

    @Transactional
    public Waiting createWaitingWithLock(WaitingCommand command, Theme theme, Time time, Member member) {
        // 1. 카운터 테이블에서 해당 예약의 row를 찾아 비관적 락
        WaitingOrderCounter counter = waitingOrderCounterRepository.findForUpdate(theme.getId(), command.date(), time.getId())
                .orElseGet(() ->
                        new WaitingOrderCounter(theme.getId(), command.date(), time.getId(), 0L)
                );

        // 2. 카운터를 1 증가시키고 저장
        counter.increaseOrder();
        waitingOrderCounterRepository.save(counter);

        return waitingRepository.save(new Waiting(member, command.date(), time, theme, counter.getLastOrder()));
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
        WaitingOrderCounter counter = waitingOrderCounterRepository.findForUpdate(
                        waiting.getTheme().getId(), waiting.getDate(), waiting.getTime().getId())
                .orElseThrow(() -> new ApplicationException(WaitingException.WAITING_ORDER_COUNTER_NOTFOUND));

        // 2. 벌크 업데이트 쿼리 -> 준영속 상태가 됨
        waitingRepository.decrementOrder(waiting.getTheme(), waiting.getDate(), waiting.getTime(), waiting.getOrder());

        waitingRepository.delete(waiting);
        counter.decreaseOrder();
        waitingOrderCounterRepository.save(counter);
    }
}
