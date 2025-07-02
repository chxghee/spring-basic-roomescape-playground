package roomescape.reservation.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.LoginMember;
import roomescape.exception.ApplicationException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.member.exception.MemberException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.presentation.response.MyReservationResponse;
import roomescape.reservation.presentation.response.ReservationResponse;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;
import roomescape.theme.exception.ThemeException;
import roomescape.time.domain.Time;
import roomescape.time.domain.TimeRepository;
import roomescape.time.exception.TimeException;
import roomescape.waiting.domain.WaitingRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final WaitingRepository waitingRepository;
    private final MemberRepository memberRepository;
    private final ThemeRepository themeRepository;
    private final TimeRepository timeRepository;

    public ReservationService(ReservationRepository reservationRepository, WaitingRepository waitingRepository,
                              MemberRepository memberRepository, ThemeRepository themeRepository, TimeRepository timeRepository) {
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
        this.memberRepository = memberRepository;
        this.themeRepository = themeRepository;
        this.timeRepository = timeRepository;
    }

    @Transactional
    public ReservationResponse save(ReservationCommand command) {
        Time time = getTime(command.time());
        Theme theme = getTheme(command.theme());
        Reservation newReservation = createReservation(command, time, theme);
        reservationRepository.save(newReservation);
        return ReservationResponse.from(newReservation);
    }

    private Reservation createReservation(ReservationCommand command, Time time, Theme theme) {
        Member member = getMember(command.memberId());

        if (member.isAdmin()) {
            return new Reservation(command.name(), command.date(), time, theme);
        }

        return new Reservation(member, command.date(), time, theme);
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
    public void deleteById(Long id) {
        reservationRepository.deleteById(id);
    }

    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll().stream()
                .map(it -> new ReservationResponse(it.getId(), it.getName(), it.getTheme().getName(), it.getDate(), it.getTime().getValue()))
                .toList();
    }

    public List<MyReservationResponse> findMyReservations(LoginMember loginMember) {
        List<MyReservationResponse> reservationList = reservationRepository.findByMemberId(loginMember.id())
                .stream()
                .map(MyReservationResponse::from)
                .toList();

        List<MyReservationResponse> waitingList = waitingRepository.findWaitingsWithRankByMemberId(loginMember.id())
                .stream()
                .map(MyReservationResponse::from)
                .toList();

        return Stream.concat(
                reservationList.stream(),
                waitingList.stream()
        ).toList();
    }
}
