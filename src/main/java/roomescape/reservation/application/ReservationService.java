package roomescape.reservation.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.ApplicationException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.infrastructure.ReservationRepository;
import roomescape.reservation.presentation.response.ReservationResponse;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;
import roomescape.theme.exception.ThemeException;
import roomescape.time.domain.Time;
import roomescape.time.domain.TimeRepository;
import roomescape.time.exception.TimeException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ThemeRepository themeRepository;
    private final TimeRepository timeRepository;


    public ReservationService(ReservationRepository reservationRepository, ThemeRepository themeRepository, TimeRepository timeRepository) {
        this.reservationRepository = reservationRepository;
        this.themeRepository = themeRepository;
        this.timeRepository = timeRepository;
    }

    @Transactional
    public ReservationResponse save(ReservationCommand reservationCommand) {
        Time time = getTime(reservationCommand.time());
        Theme theme = getTheme(reservationCommand.theme());
        Reservation newReservation = new Reservation(reservationCommand.name(), reservationCommand.date(), time, theme);
        reservationRepository.save(newReservation);
        return ReservationResponse.from(newReservation);
    }

    private Theme getTheme(Long id) {
        return themeRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ThemeException.THEME_NOT_FOUND));
    }

    private Time getTime(Long id) {
        return timeRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(TimeException.TIME_NOT_FOUND));
    }

    @Transactional
    public void deleteById(Long id) {
        reservationRepository.deleteById(id);
    }

    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll().stream()
                .map(it -> new ReservationResponse(it.getId(), it.getName(), it.getTheme().getName(), it.getDate(), it.getTime().getValue()))
                .toList();
    }
}
