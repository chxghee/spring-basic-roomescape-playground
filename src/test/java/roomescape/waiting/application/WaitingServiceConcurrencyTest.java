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
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;
import roomescape.time.domain.Time;
import roomescape.time.domain.TimeRepository;
import roomescape.waiting.application.command.WaitingCommand;
import roomescape.waiting.domain.repository.WaitingRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class WaitingSaveServiceTest {

    @Autowired
    private WaitingRepository waitingRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TimeRepository timeRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private WaitingService waitingService;

    private Time time;
    private Theme theme;
    private String date = "2025-10-21";

    @BeforeEach
    void setUp() {
        time = timeRepository.save(new Time("08:00"));
        theme = themeRepository.save(new Theme("테마0", "공포테마"));
    }

    @Test
    void 동시성_테스트_비관적락이_제대로_동작하는지_확인() throws InterruptedException {
        // given
        int threadCount = 500;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // when - 동시에 여러 대기 요청
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    Member member = memberRepository.save(new Member("user" + index, "user" + index + "@email.com", "1234", Role.USER));
                    WaitingCommand command = new WaitingCommand(date, member.getId(), theme.getId(), time.getId());

                    waitingService.save(command);
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

        // then
        System.out.println("Success: " + successCount.get() + ", Failure: " + failureCount.get());

        // 실제 요청 수와 저장한 waiting 개수가 일치하는지 확인
        long waitingCount = waitingRepository.count();
        assertThat(waitingCount).isEqualTo(threadCount);
    }
}
