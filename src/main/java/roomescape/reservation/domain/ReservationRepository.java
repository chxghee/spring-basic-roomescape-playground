package roomescape.reservation.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import roomescape.exception.ApplicationException;
import roomescape.member.domain.Member;
import roomescape.reservation.exception.ReservationException;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.Time;

import java.util.List;

@Repository
public class ReservationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Reservation> findAll() {
        return entityManager.createQuery("select r from Reservation r " +
                        "join fetch r.time " +
                        "join fetch r.theme", Reservation.class)
                .getResultList();
    }

    public Reservation save(Reservation reservation) {
        entityManager.persist(reservation);
        return reservation;
    }

    public void deleteById(Long id) {
        Reservation reservation = entityManager.find(Reservation.class, id);
        if (reservation == null) {
                throw new ApplicationException(ReservationException.RESERVATION_NOT_FOUND);
        }
        entityManager.remove(reservation);
    }

    public List<Reservation> findByDateAndThemeId(String date, Long themeId) {
        return entityManager.createQuery("select r from Reservation r " +
                "join fetch r.theme t " +
                "join fetch r.time ti " +
                "where r.date = :data " +
                "and t.id = :themId", Reservation.class)
                .setParameter("data", date)
                .setParameter("themId", themeId)
                .getResultList();
    }

    public List<Reservation> findByMemberId(Long memberId) {
        String jpql = "select r from Reservation r " +
                "join fetch r.theme t " +
                "join fetch r.time ti " +
                "join fetch r.member m " +
                "where m.id = :memberId";

        return entityManager.createQuery(jpql, Reservation.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public Boolean existsByMemberAndDateAndTimeAndTheme(Member member, String date, Time time, Theme theme) {
        String jpql = "select case when exists (" +
                "select r from Reservation r " +
                "where r.member = :member " +
                "and r.date = :date " +
                "and r.time = :time " +
                "and r.theme = :theme" +
                ") then true else false end";

        return entityManager.createQuery(jpql, Boolean.class)
                .setParameter("member", member)
                .setParameter("date", date)
                .setParameter("time", time)
                .setParameter("theme", theme)
                .getSingleResult();
    }
}
