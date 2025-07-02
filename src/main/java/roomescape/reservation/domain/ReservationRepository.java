package roomescape.reservation.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import roomescape.exception.ApplicationException;
import roomescape.reservation.exception.ReservationException;

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
        return entityManager.createQuery("select r from Reservation r " +
                "join fetch r.theme t " +
                "join fetch r.time ti " +
                "join fetch r.member m " +
                "where m.id = :memberId", Reservation.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }
}
