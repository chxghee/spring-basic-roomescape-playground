package roomescape.reservation.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.LoginMember;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.reservation.application.command.ReservationCommand;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.presentation.response.MyReservationResponse;
import roomescape.reservation.presentation.response.ReservationResponse;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;
import roomescape.time.domain.Time;
import roomescape.time.domain.TimeRepository;
import roomescape.waiting.domain.repository.WaitingRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
public class ReservationService {

    private final ReservationSaveService reservationSaveService;
    private final ReservationRepository reservationRepository;
    private final WaitingRepository waitingRepository;
    private final MemberRepository memberRepository;
    private final ThemeRepository themeRepository;
    private final TimeRepository timeRepository;

    public ReservationService(ReservationSaveService reservationSaveService, ReservationRepository reservationRepository,
                              WaitingRepository waitingRepository, MemberRepository memberRepository,
                              ThemeRepository themeRepository, TimeRepository timeRepository) {
        this.reservationSaveService = reservationSaveService;
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
        this.memberRepository = memberRepository;
        this.themeRepository = themeRepository;
        this.timeRepository = timeRepository;
    }

    public ReservationResponse save(ReservationCommand command) {
        Time time = timeRepository.getTimeById(command.time());
        Theme theme = themeRepository.getThemeById(command.theme());
        Member member = memberRepository.getMemberById(command.memberId());
        Reservation newReservation = createReservation(command, time, theme, member);
        return reservationSaveService.saveReservation(newReservation);
    }

    private Reservation createReservation(ReservationCommand command, Time time, Theme theme, Member member) {
        if (member.isAdmin()) {
            return new Reservation(command.name(), command.date(), time, theme);
        }
        return new Reservation(member, command.date(), time, theme);
    }

    @Transactional
    public void deleteById(Long id) {
        Reservation reservation = reservationRepository.getReservationById(id);
        reservationRepository.delete(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll().stream()
                .map(it -> new ReservationResponse(it.getId(), it.getName(), it.getTheme().getName(), it.getDate(), it.getTime().getValue()))
                .toList();
    }

    public List<MyReservationResponse> findMyReservations(LoginMember loginMember) {
        List<MyReservationResponse> reservationList = reservationRepository.findByMember_Id(loginMember.id())
                .stream()
                .map(MyReservationResponse::from)
                .toList();

        List<MyReservationResponse> waitingList = waitingRepository.findByMember_Id(loginMember.id())
                .stream()
                .map(MyReservationResponse::from)
                .toList();

        return Stream.concat(
                reservationList.stream(),
                waitingList.stream()
        ).toList();
    }
}
