package roomescape.time.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.exception.ApplicationException;
import roomescape.time.exception.TimeException;

import java.util.List;
import java.util.Optional;

public interface TimeRepository extends JpaRepository<Time, Long> {

    @Override
    @Query("select t from Time t where t.deleted = false")
    List<Time> findAll();

    @Override
    @Query("select t from Time t where t.deleted = false and t.id = :id")
    Optional<Time> findById(@Param("id") Long id);

    default Time getTimeById(Long id) {
        return findById(id)
                .orElseThrow(() -> new ApplicationException(TimeException.TIME_NOT_FOUND));
    }
}
