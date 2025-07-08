package roomescape.waiting.domain.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import roomescape.exception.ApplicationException;
import roomescape.member.domain.Member;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.Time;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.exception.WaitingException;

import java.util.List;

@Repository
public interface WaitingRepository extends JpaRepository<Waiting, Long> {

    @EntityGraph(attributePaths = {"theme", "time"})
    List<Waiting> findByMember_Id(Long memberId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Waiting w " +
            "set w.order = w.order - 1 " +
            "where w.theme = :theme " +
            "and w.date = :date " +
            "and w.time = :time " +
            "and w.order > :deletedOrder")
    void decrementOrder(@Param("theme") Theme theme, @Param("date") String date,
                        @Param("time") Time time, @Param("deletedOrder") Long deletedOrder);

    Boolean existsByMemberAndDateAndTimeAndTheme(Member member, String date, Time time, Theme theme);

    default Waiting getWaitingById(Long waitingId) {
        return findById(waitingId)
                .orElseThrow(() -> new ApplicationException(WaitingException.WAITING_NOT_FOUND));
    }
}
