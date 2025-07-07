package roomescape.waiting.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class WaitingOrderCounterRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public WaitingOrderCounter save(WaitingOrderCounter counter) {
        if (counter.getId() == null) {
            entityManager.persist(counter);
        } else {
            entityManager.merge(counter);       // 준영속 상태일 때
        }
        return counter;
    }

    public Optional<WaitingOrderCounter> findForUpdate(Long themeId, String date, Long timeId) {
        String jpql = "select c FROM WaitingOrderCounter c " +
                "where c.themeId = :themeId " +
                "and c.date = :date " +
                "and c.timeId = :timeId";

        return entityManager.createQuery(jpql, WaitingOrderCounter.class)
                .setParameter("themeId", themeId)
                .setParameter("date", date)
                .setParameter("timeId", timeId)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultList()
                .stream().findFirst();
    }
}
