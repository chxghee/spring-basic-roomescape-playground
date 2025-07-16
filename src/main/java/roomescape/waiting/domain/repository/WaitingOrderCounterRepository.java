package roomescape.waiting.domain.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import roomescape.waiting.domain.WaitingOrderCounter;

import java.util.Optional;

@Repository
public interface WaitingOrderCounterRepository extends JpaRepository<WaitingOrderCounter, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c FROM WaitingOrderCounter c " +
            "where c.themeId = :themeId " +
            "and c.date = :date " +
            "and c.timeId = :timeId")
    Optional<WaitingOrderCounter> findByThemeIdAndDateAndTimeIdWithLock(Long themeId, String date, Long timeId);
}
