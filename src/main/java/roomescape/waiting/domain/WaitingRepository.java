package roomescape.waiting.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import roomescape.exception.ApplicationException;
import roomescape.member.domain.Member;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.Time;
import roomescape.waiting.exception.WaitingException;

import java.util.List;
import java.util.Optional;

@Repository
public class WaitingRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Waiting> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Waiting.class, id));
    }

    public Waiting getWaitingById(Long waitingId) {
        return findById(waitingId)
                .orElseThrow(() -> new ApplicationException(WaitingException.WAITING_NOT_FOUND));
    }

    public Waiting save(Waiting waiting) {
        entityManager.persist(waiting);
        return waiting;
    }

    public Boolean existsByMemberAndDateAndTimeAndTheme(Member member, String date, Time time, Theme theme) {
        String jpql = "select case when exists (" +
                            "select w from Waiting w " +
                            "where w.member = :member " +
                            "and w.date = :date " +
                            "and w.time = :time " +
                            "and w.theme = :theme" +
                    ") then true else false end";

        return entityManager.createQuery(jpql, Boolean.class)
                .setParameter("member", member)
                .setParameter("date", date)
                .setParameter("time", time)
                .setParameter("theme", theme)
                .getSingleResult();
    }

    public void delete(Waiting waiting) {
        if (entityManager.contains(waiting)) {
            entityManager.remove(waiting);
        } else {
            Waiting merged = entityManager.merge(waiting);
            entityManager.remove(merged);
        }
    }

    public List<Waiting> findByMemberId(Long memberId) {
        String jpql = "select w from Waiting w " +
                "join fetch w.theme t " +
                "join fetch w.time ti " +
                "join fetch w.member m " +
                "where m.id = :memberId";

        return entityManager.createQuery(jpql, Waiting.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public void decrementOrder(Theme theme, String date, Time time, Long deletedOrder) {
        // 벌크 쿼리 전 영속 플러시
        entityManager.flush();

        String jpql = "update Waiting w " +
                "set w.order = w.order - 1 " +
                "where w.theme = :theme " +
                "and w.date = :date " +
                "and w.time = :time " +
                "and w.order > :deletedOrder";

        entityManager.createQuery(jpql)
                .setParameter("theme", theme)
                .setParameter("date", date)
                .setParameter("time", time)
                .setParameter("deletedOrder", deletedOrder)
                .executeUpdate();

        // 벌크 쿼리 후 영속 비우기
        entityManager.clear();
    }
}
