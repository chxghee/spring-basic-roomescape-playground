package roomescape.time.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.ApplicationException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.infrastructure.ReservationDao;
import roomescape.time.exception.TimeException;
import roomescape.time.presentation.AvailableTime;
import roomescape.time.domain.Time;
import roomescape.time.domain.TimeRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TimeService {

    private TimeRepository timeRepository;
    private ReservationDao reservationDao;

    public TimeService(TimeRepository timeRepository, ReservationDao reservationDao) {
        this.timeRepository = timeRepository;
        this.reservationDao = reservationDao;
    }

    public List<AvailableTime> getAvailableTime(String date, Long themeId) {
        List<Reservation> reservations = reservationDao.findByDateAndThemeId(date, themeId);
        List<Time> times = timeRepository.findAll();

        return times.stream()
                .map(time -> new AvailableTime(
                        time.getId(),
                        time.getValue(),
                        reservations.stream()
                                .anyMatch(reservation -> reservation.getTime().getId().equals(time.getId()))
                ))
                .toList();
    }

    public List<Time> findAll() {
        return timeRepository.findAll();
    }

    @Transactional
    public Time save(Time time) {
        return timeRepository.save(time);
    }

    @Transactional
    public void deleteById(Long id) {
        Time time = getTime(id);
        time.delete();
    }

    private Time getTime(Long id) {
        return timeRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(TimeException.TIME_NOT_FOUND));
    }
}
