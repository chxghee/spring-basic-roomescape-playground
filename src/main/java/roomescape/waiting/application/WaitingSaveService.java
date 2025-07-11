package roomescape.waiting.application;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.ApplicationException;
import roomescape.member.domain.Member;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.Time;
import roomescape.waiting.application.command.WaitingCommand;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.domain.WaitingOrderCounter;
import roomescape.waiting.domain.repository.WaitingOrderCounterRepository;
import roomescape.waiting.domain.repository.WaitingRepository;
import roomescape.waiting.exception.WaitingException;

@Service
public class WaitingSaveService {

    private final WaitingRepository waitingRepository;
    private final WaitingOrderCounterRepository waitingOrderCounterRepository;

    public WaitingSaveService(WaitingRepository waitingRepository, WaitingOrderCounterRepository waitingOrderCounterRepository) {
        this.waitingRepository = waitingRepository;
        this.waitingOrderCounterRepository = waitingOrderCounterRepository;
    }

    @Transactional
    public Waiting createWaiting(WaitingCommand command, Theme theme, Time time, Member member) {
        int maxAttempts = 2;
        int attempts = 0;
        // 카운터 생성 충돌시 재시도 로직
        while (attempts < maxAttempts) {
            attempts++;
            try {
                return createWaitingWithLock(command, theme, time, member);
            } catch (DataIntegrityViolationException e) {
                if (attempts >= maxAttempts) throw new ApplicationException(WaitingException.RETRY_WAITING_ORDER_COUNTER_FAILED);
            }
        }
        throw new ApplicationException(WaitingException.RETRY_WAITING_ORDER_COUNTER_FAILED);
    }

    private Waiting createWaitingWithLock(WaitingCommand command, Theme theme, Time time, Member member) {
        // 1. 카운터 테이블에서 해당 예약의 row를 찾아 락
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
