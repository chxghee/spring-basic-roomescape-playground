package roomescape.waiting.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.member.domain.Role;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;
import roomescape.time.domain.Time;
import roomescape.time.domain.TimeRepository;
import roomescape.waiting.application.command.WaitingCommand;
import roomescape.waiting.domain.repository.WaitingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class WaitingServiceConcurrencyTest {

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

    private final int joinedMemberCount = 500;
    private final List<Member> joinedMembers = new ArrayList<>();
    private Time time;
    private Theme theme;
    private String date = "2025-10-21";

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
    void 동시에_많은_사용자가_예약대기_요청을_해도_요청한_사용자_수_만큼_대기_순번이_정해져야_한다() throws InterruptedException {

        int threadCount = joinedMemberCount;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    WaitingCommand command = new WaitingCommand(date, joinedMembers.get(index).getId(), theme.getId(), time.getId());

                    waitingService.save(command);       // 예약 대기 요청
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

        // 실제 요청 수와 저장한 waiting 개수가 일치하는지 확인
        long waitingCount = waitingRepository.count();
        assertThat(waitingCount).isEqualTo(threadCount);
    }
}
