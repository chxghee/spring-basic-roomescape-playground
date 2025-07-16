package roomescape.reservation.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import roomescape.exception.ApplicationException;
import roomescape.member.domain.Member;
import roomescape.reservation.exception.ReservationException;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.Time;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Override
    @EntityGraph(attributePaths = {"time", "theme"})
    List<Reservation> findAll();

    @EntityGraph(attributePaths = {"time", "theme"})
    List<Reservation> findByDateAndTheme_Id(String date, Long themeId);

    @EntityGraph(attributePaths = {"time", "theme"})
    List<Reservation> findByMember_Id(Long memberId);

    boolean existsByMemberAndDateAndTimeAndTheme(Member member, String date, Time time, Theme theme);

    default Reservation getReservationById(Long id) {
        return findById(id)
                .orElseThrow(() -> new ApplicationException(ReservationException.RESERVATION_NOT_FOUND));
    }
}
