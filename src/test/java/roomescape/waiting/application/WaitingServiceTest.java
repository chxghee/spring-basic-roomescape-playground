package roomescape.waiting.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.AuthException;
import roomescape.auth.LoginMember;
import roomescape.exception.ApplicationException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.member.domain.Role;
import roomescape.reservation.domain.Reservation;
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

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

@SpringBootTest
@Transactional
class WaitingServiceTest {

    @Autowired
    private WaitingService waitingService;
    @Autowired
    private WaitingRepository waitingRepository;
    @Autowired
    private WaitingOrderCounterRepository waitingOrderCounterRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TimeRepository timeRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    private Member member;
    private Time time;
    private Theme theme;

    private String date = "2025-10-21";

    @BeforeEach
    void setUp() {
        member = memberRepository.save(new Member("changhee", "asd@email.com", "1234", Role.USER));
        time = timeRepository.save(new Time("08:00"));
        theme = themeRepository.save(new Theme("테마0", "공포테마"));
    }

    @Test
    void 사용자가_해당_시간에_예약을_이미_했으면_예약대기_요청시_예외가_발생해야_한다() {
        reservationRepository.save(new Reservation(member, date, time, theme));
        WaitingCommand waitingCommand = new WaitingCommand(date, member.getId(), theme.getId(), time.getId());

        assertThatThrownBy(() -> waitingService.save(waitingCommand))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(ReservationException.DUPLICATE_RESERVATION_REQUEST.getDetail());
    }

    @Test
    void 사용자가_해당_시간에_예약대기를_이미_했으면_예약대기_요청시_예외가_발생해야_한다() {
        WaitingCommand waitingCommand = new WaitingCommand(date, member.getId(), theme.getId(), time.getId());
        waitingService.save(waitingCommand);

        assertThatThrownBy(() -> waitingService.save(waitingCommand))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(WaitingException.DUPLICATE_WAITING_REQUEST.getDetail());
    }

    @Test
    void 예약대기_취소는_로그인한_사용자_본인이_아니면_예외가_발생해야_한다() {
        WaitingCommand waitingCommand = new WaitingCommand(date, member.getId(), theme.getId(), time.getId());
        WaitingResponse otherPersonWaiting = waitingService.save(waitingCommand);

        Member heechang = memberRepository.save(new Member("heechang", "xcv@email.com", "1234", Role.USER));
        LoginMember loginMember = new LoginMember(heechang.getId());

        assertThatThrownBy(() -> waitingService.delete(loginMember, otherPersonWaiting.id()))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(AuthException.FORBIDDEN_ACCESS.getDetail());
    }

    @Test
    void 예약대기_성공시_예약대기_순번을_알_수_있다() {
        WaitingCommand waitingCommand1 = new WaitingCommand(date, member.getId(), theme.getId(), time.getId());
        WaitingResponse firstWaiting = waitingService.save(waitingCommand1);

        Member heechang = memberRepository.save(new Member("heechang", "xcv@email.com", "1234", Role.USER));
        WaitingCommand waitingCommand2 = new WaitingCommand(date, heechang.getId(), theme.getId(), time.getId());
        WaitingResponse secondWaiting = waitingService.save(waitingCommand2);

        assertSoftly(softly -> {
            assertThat(firstWaiting.waitingNumber()).isEqualTo(1L);
            assertThat(secondWaiting.waitingNumber()).isEqualTo(2L);
        });
    }

    @Test
    void 예약대기_취소시_예약대기_순번이_당겨져야_한다() {
        WaitingCommand waitingCommand1 = new WaitingCommand(date, member.getId(), theme.getId(), time.getId());
        WaitingResponse firstWaiting = waitingService.save(waitingCommand1);

        Member heechang = memberRepository.save(new Member("heechang", "xcv@email.com", "1234", Role.USER));
        WaitingCommand waitingCommand2 = new WaitingCommand(date, heechang.getId(), theme.getId(), time.getId());
        WaitingResponse secondWaiting = waitingService.save(waitingCommand2);

        Member hee = memberRepository.save(new Member("hee", "cvb@email.com", "1234", Role.USER));
        WaitingCommand waitingCommand3 = new WaitingCommand(date, hee.getId(), theme.getId(), time.getId());
        WaitingResponse thirdWaiting = waitingService.save(waitingCommand3);

        // 가장 첫번째 예약 대기 삭제
        LoginMember loginMember = new LoginMember(member.getId());
        waitingService.delete(loginMember, firstWaiting.id());

        Waiting heechangsWaitings = waitingRepository.findByMember_Id(heechang.getId()).get(0);
        Waiting heesWaitings = waitingRepository.findByMember_Id(hee.getId()).get(0);
        WaitingOrderCounter counter = waitingOrderCounterRepository.findByThemeIdAndDateAndTimeIdWithLock(theme.getId(), date, time.getId()).get();

        assertSoftly(softly -> {
            // 삭제 이전 상태
            assertThat(firstWaiting.waitingNumber()).isEqualTo(1L);
            assertThat(secondWaiting.waitingNumber()).isEqualTo(2L);
            assertThat(thirdWaiting.waitingNumber()).isEqualTo(3L);

            // 삭제 이후 상태
            assertThat(heechangsWaitings.getOrder()).isEqualTo(1L);
            assertThat(heesWaitings.getOrder()).isEqualTo(2L);
            assertThat(counter.getLastOrder()).isEqualTo(2L);
        });
    }
}
