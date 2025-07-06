package roomescape.waiting.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.AuthException;
import roomescape.auth.LoginMember;
import roomescape.exception.ApplicationException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.member.exception.MemberException;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.exception.ReservationException;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;
import roomescape.theme.exception.ThemeException;
import roomescape.time.domain.Time;
import roomescape.time.domain.TimeRepository;
import roomescape.time.exception.TimeException;
import roomescape.waiting.application.command.WaitingCommand;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.domain.WaitingRepository;
import roomescape.waiting.exception.WaitingException;
import roomescape.waiting.presentation.response.WaitingResponse;

@Service
@Transactional(readOnly = true)
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final ThemeRepository themeRepository;
    private final TimeRepository timeRepository;

    public WaitingService(WaitingRepository waitingRepository, ReservationRepository reservationRepository,
                          MemberRepository memberRepository, ThemeRepository themeRepository, TimeRepository timeRepository) {
        this.waitingRepository = waitingRepository;
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.themeRepository = themeRepository;
        this.timeRepository = timeRepository;
    }

    @Transactional
    public WaitingResponse save(WaitingCommand command) {
        Time time = getTime(command.time());
        Theme theme = getTheme(command.theme());
        Member member = getMember(command.memberId());

        validateDuplicateRequest(command.date(), member, time, theme);

        Long waitingCount = waitingRepository.countByDateAndTimeAndTheme(command.date(), time, theme) + 1;
        Waiting newWaiting = waitingRepository.save(new Waiting(member, command.date(), time, theme));
        return WaitingResponse.from(newWaiting, waitingCount);
    }

    private void validateDuplicateRequest(String date, Member member, Time time, Theme theme) {
        if (waitingRepository.existsByMemberAndDateAndTimeAndTheme(member, date, time, theme)) {
            throw new ApplicationException(WaitingException.DUPLICATE_WAITING_REQUEST);
        }

        if (reservationRepository.existsByMemberAndDateAndTimeAndTheme(member, date, time, theme)) {
            throw new ApplicationException(ReservationException.DUPLICATE_RESERVATION_REQUEST);
        }
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(MemberException.MEMBER_NOT_FOUND));
    }

    private Theme getTheme(Long id) {
        return themeRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ThemeException.THEME_NOT_FOUND));
    }

    private Time getTime(Long id) {
        return timeRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(TimeException.TIME_NOT_FOUND));
    }

    @Transactional
    public void delete(LoginMember loginMember, Long waitingId) {
        Waiting waiting = getWaiting(waitingId);

        if (!waiting.belongsTo(loginMember.id())) {
            throw new ApplicationException(AuthException.FORBIDDEN_ACCESS);
        }
        waitingRepository.delete(waiting);
    }

    private Waiting getWaiting(Long waitingId) {
        return waitingRepository.findById(waitingId)
                .orElseThrow(() -> new ApplicationException(WaitingException.WAITING_NOT_FOUND));
    }
}
