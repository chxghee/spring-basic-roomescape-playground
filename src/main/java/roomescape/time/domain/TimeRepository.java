package roomescape.time.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

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

    public List<Time> findAll() {
        return entityManager.createQuery("select t from Time t where t.deleted = false ", Time.class)
                .getResultList();
    }

    public Time save(Time time) {
        entityManager.persist(time);
        return time;
    }
}
