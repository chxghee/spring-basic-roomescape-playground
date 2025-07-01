package roomescape.reservation.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.LoginMember;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.domain.MemberRepository;
import roomescape.reservation.presentation.request.ReservationRequest;
import roomescape.reservation.presentation.response.ReservationResponse;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private MemberRepository memberRepository;

    private Member user;

    @BeforeEach
    void setUp() {
        user = memberRepository.save(new Member("유저", "qwe@email.com", "1234", Role.USER));
    }

    @Test
    void 예약요청에_이름값이_있으면_해당_이름으로_예약을_생성해야_한다() {
        ReservationRequest request = new ReservationRequest("2024-03-01", "다른유저", 1L, 1L);
        LoginMember loginMember = new LoginMember(user.getId(), user.getName(), user.getRole());

        ReservationCommand command = request.toCommand(loginMember);
        ReservationResponse result = reservationService.save(command);

        assertThat(result.name()).isEqualTo("다른유저");
    }

    @Test
    void 예약요청에_이름값이_없으면_로그인한_유저이름으로_예약이_생성되어야_한다() {
        ReservationRequest request = new ReservationRequest("2024-03-01", null,1L, 1L);
        LoginMember loginMember = new LoginMember(user.getId(), user.getName(), user.getRole());

        ReservationCommand command = request.toCommand(loginMember);
        ReservationResponse result = reservationService.save(command);

        assertThat(result.name()).isEqualTo("유저");
    }


}
