package roomescape.waiting.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.domain.Member;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.Time;
import roomescape.waiting.application.command.WaitingCommand;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.domain.WaitingOrderCounter;
import roomescape.waiting.domain.repository.WaitingOrderCounterRepository;
import roomescape.waiting.domain.repository.WaitingRepository;

@Service
public class WaitingSaveService {

    private final WaitingRepository waitingRepository;
    private final WaitingOrderCounterRepository waitingOrderCounterRepository;

    public WaitingSaveService(WaitingRepository waitingRepository, WaitingOrderCounterRepository waitingOrderCounterRepository) {
        this.waitingRepository = waitingRepository;
        this.waitingOrderCounterRepository = waitingOrderCounterRepository;
    }

    @Transactional
    public Waiting createWaitingWithLock(WaitingCommand command, Theme theme, Time time, Member member) {
        // 1. 카운터 테이블에서 해당 예약의 row를 찾아 락 (없다면 카운터 생성)
        WaitingOrderCounter counter = waitingOrderCounterRepository.findByThemeIdAndDateAndTimeIdWithLock(theme.getId(), command.date(), time.getId())
                .orElseGet(() ->
                        new WaitingOrderCounter(theme.getId(), command.date(), time.getId(), 0L)
                );

        // 2. 카운터를 1 증가시키고 저장
        counter.increaseOrder();
        waitingOrderCounterRepository.save(counter);
        return waitingRepository.save(new Waiting(member, command.date(), time, theme, counter.getLastOrder()));
    }
}
