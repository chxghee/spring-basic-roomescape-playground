package roomescape.theme.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import roomescape.exception.ApplicationException;
import roomescape.theme.exception.ThemeException;

import java.util.List;
import java.util.Optional;

@Repository
public class ThemeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Theme> findById(Long id) {
         return entityManager.createQuery("select t from Theme t where t.deleted = false and t.id = :id", Theme.class)
                 .setParameter("id", id)
                 .getResultList()
                 .stream()
                 .findFirst();
    }

    public Theme getThemeById(Long id) {
        return findById(id)
                .orElseThrow(() -> new ApplicationException(ThemeException.THEME_NOT_FOUND));
    }

    public List<Theme> findAll() {
        return entityManager.createQuery("select t from Theme t where t.deleted = false", Theme.class)
                .getResultList();
    }

    public Theme save(Theme theme) {
        entityManager.persist(theme);
        return theme;
    }

}
