package roomescape.time.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import roomescape.exception.ApplicationException;
import roomescape.time.exception.TimeException;

import java.util.List;
import java.util.Optional;

@Repository
public class TimeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Time> findById(Long id) {
        return entityManager.createQuery("SELECT t FROM Time t WHERE t.deleted = false and t.id = :id", Time.class)
                .setParameter("id", id)
                .getResultList()
                .stream()
                .findFirst();
    }

    public Time getTimeById(Long id) {
        return findById(id)
                .orElseThrow(() -> new ApplicationException(TimeException.TIME_NOT_FOUND));
    }

    public List<Time> findAll() {
        return entityManager.createQuery("select t from Time t where t.deleted = false ", Time.class)
                .getResultList();
    }

    public Time save(Time time) {
        entityManager.persist(time);
        return time;
    }
}
