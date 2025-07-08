package roomescape.theme.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.exception.ApplicationException;
import roomescape.theme.exception.ThemeException;

import java.util.List;
import java.util.Optional;


public interface ThemeRepository extends JpaRepository<Theme, Long> {

    @Override
    @Query("select t from Theme t where t.deleted = false")
    List<Theme> findAll();

    @Override
    @Query("select t from Theme t where t.deleted = false and t.id = :id")
    Optional<Theme> findById(@Param("id") Long id);

    default Theme getThemeById(Long id) {
        return findById(id)
                .orElseThrow(() -> new ApplicationException(ThemeException.THEME_NOT_FOUND));
    }
}
