package roomescape.reservation.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.LoginMember;
import roomescape.exception.ApplicationException;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.domain.MemberRepository;
import roomescape.reservation.application.command.ReservationCommand;
import roomescape.reservation.exception.ReservationException;
import roomescape.reservation.presentation.request.ReservationRequest;
import roomescape.reservation.presentation.response.MyReservationResponse;
import roomescape.reservation.presentation.response.ReservationResponse;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;
import roomescape.time.domain.Time;
import roomescape.time.domain.TimeRepository;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.domain.repository.WaitingRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

@SpringBootTest
@Transactional
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TimeRepository timeRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private WaitingRepository waitingRepository;

    private Member user;
    private Member admin;
    private Time time;
    private Theme theme;
    private final String date = "2025-10-21";

    @BeforeEach
    void setUp() {
        user = memberRepository.save(new Member("유저", "qwe@email.com", "1234", Role.USER));
        admin = memberRepository.save(new Member("어드민 유저", "ert@email.com", "1234", Role.ADMIN));
        time = timeRepository.save(new Time("08:00"));
        theme = themeRepository.save(new Theme("테마0", "공포테마"));
    }

    @Test
    void 로그인한_유저의_권한이_ADMIN이라면_요청에_입력된_이름으로_예약을_생성해야_한다() {
        ReservationRequest request = new ReservationRequest("2024-03-01", "다른유저", theme.getId(), time.getId());
        LoginMember loginMember = new LoginMember(admin.getId());

        ReservationCommand command = request.toCommand(loginMember);
        ReservationResponse result = reservationService.save(command);

        assertThat(result.name()).isEqualTo("다른유저");
    }

    @Test
    void 로그인한_유저의_권한이_USER라면_유저이름으로_예약이_생성되어야_한다() {
        ReservationRequest request = new ReservationRequest("2024-03-01", null, theme.getId(), time.getId());
        LoginMember loginMember = new LoginMember(user.getId());

        ReservationCommand command = request.toCommand(loginMember);
        ReservationResponse result = reservationService.save(command);

        assertThat(result.name()).isEqualTo("유저");
    }

    @Test
    void 이미_동일시간_동일테마가_예약되었다면_예외가_발생해야_한다() {
        ReservationCommand command1 = new ReservationCommand(date, admin.getId(), null, theme.getId(), time.getId());
        reservationService.save(command1);


        ReservationRequest request = new ReservationRequest(date, null, theme.getId(), time.getId());
        LoginMember loginMember = new LoginMember(user.getId());

        ReservationCommand command2 = request.toCommand(loginMember);
        assertThatThrownBy(() -> reservationService.save(command2))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(ReservationException.DUPLICATE_RESERVATION_REQUEST.getDetail());
    }

    @Test
    void 나의_예약_현황을_조회_할_수_있다() {
        Theme otherTheme = themeRepository.save(new Theme("테마", "추리테마"));

        ReservationRequest firstRequest = new ReservationRequest("2024-03-01", null, theme.getId(), time.getId());
        ReservationRequest secondRequest = new ReservationRequest("2024-03-01", null, otherTheme.getId(), time.getId());
        LoginMember loginMember = new LoginMember(user.getId());

        ReservationCommand command1 = firstRequest.toCommand(loginMember);
        ReservationResponse firstReservation = reservationService.save(command1);

        ReservationCommand command2 = secondRequest.toCommand(loginMember);
        ReservationResponse secondReservation = reservationService.save(command2);

        List<MyReservationResponse> myReservations = reservationService.findMyReservations(loginMember);
        assertSoftly(softly -> {
            assertThat(myReservations).extracting("id")
                    .containsExactly(firstReservation.id(), secondReservation.id());
            assertThat(myReservations).extracting("status")
                    .containsOnly("예약");
        });
    }

    @Test
    void 나의_에약_현황을_조회시_예약_대기_목록_또한_대기순위와_함께_조회할_수_있다() {
        ReservationCommand command = new ReservationCommand(date, admin.getId(), null, theme.getId(), time.getId());
        reservationService.save(command);

        Waiting waiting = new Waiting(user, date, time, theme, 1L);
        waitingRepository.save(waiting);
        LoginMember loginMember = new LoginMember(user.getId());

        List<MyReservationResponse> myReservations = reservationService.findMyReservations(loginMember);
        assertSoftly(softly -> {
            assertThat(myReservations.size()).isEqualTo(1);
            assertThat(myReservations).extracting("status")
                    .containsOnly("1번째 예약대기");
        });
    }
}
