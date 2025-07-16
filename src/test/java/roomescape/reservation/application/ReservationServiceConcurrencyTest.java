package roomescape.reservation.application;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.member.domain.Role;
import roomescape.reservation.application.command.ReservationCommand;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;
import roomescape.time.domain.Time;
import roomescape.time.domain.TimeRepository;
import roomescape.waiting.domain.repository.WaitingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ReservationServiceConcurrencyTest {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TimeRepository timeRepository;
    @Autowired
    private ThemeRepository themeRepository;

    private final int joinedMemberCount = 500;
    private final List<Member> joinedMembers = new ArrayList<>();
    private Time time;
    private Theme theme;
    private final String date = "2025-10-21";
    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        for (int i = 0; i < joinedMemberCount; i++) {
            Member member = memberRepository.save(new Member("유저" + i, "mail" + i + "@email.com", "1234", Role.USER));
            joinedMembers.add(member);
        }
        time = timeRepository.save(new Time("08:00"));
        theme = themeRepository.save(new Theme("테마0", "공포테마"));
    }

    @Test
    void 동시에_많은_예약_요청이_발생해도_단_하나의_예약만_성공해야_한다() throws InterruptedException {

        final int threadCount = joinedMemberCount;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executorService.submit(() -> {

                try {
                    ReservationCommand reservationCommand = new ReservationCommand(date, joinedMembers.get(index).getId(), "", theme.getId(), time.getId());
                    reservationService.save(reservationCommand);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    System.out.println("Thread " + index + " failed: " + e.getMessage());
                } finally {

                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        System.out.println("Success: " + successCount.get() + ", Failure: " + failureCount.get());

        // 테스트 코드로 생성한 예약 수 = 전체 예약 개수 - 4개 (data.sql로 사전 저장된 예약 개수)
        long reservationCount = reservationRepository.count() - 4;

        assertSoftly(softy -> {
            softy.assertThat(successCount.get()).isEqualTo(1);
            softy.assertThat(failureCount.get()).isEqualTo(499);
            softy.assertThat(reservationCount).isEqualTo(1L);
        });
    }
}
