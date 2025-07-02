package roomescape.waiting.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import roomescape.member.domain.Member;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.Time;

import java.util.List;
import java.util.Optional;

@Repository
public class WaitingRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Waiting> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Waiting.class, id));
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

    public Long countByDateAndTimeAndTheme(String date, Time time, Theme theme) {
        String jpql = "select count(w) from Waiting w " +
                "where w.date = :date " +
                "and w.time = :time " +
                "and w.theme = :theme";

        return entityManager.createQuery(jpql, Long.class)
                .setParameter("date", date)
                .setParameter("time", time)
                .setParameter("theme", theme)
                .getSingleResult();
    }

    public void delete(Waiting waiting) {
        entityManager.remove(waiting);
    }

    public List<WaitingWithRank> findWaitingsWithRankByMemberId(Long memberId) {

        return entityManager.createQuery("select new roomescape.waiting.domain.WaitingWithRank(" +
                                "w, " +
                                "(" +
                                    "select count(w2) + 1 from Waiting w2 " +
                                    "where w2.theme = w.theme " +
                                    "and w2.date = w.date " +
                                    "and w2.time = w.time " +
                                    "and w2.id < w.id" +
                                ")) " +
                                "from Waiting w " +
                                "where w.member.id = :memberId"
                , WaitingWithRank.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }
}
