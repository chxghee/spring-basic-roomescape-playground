package roomescape.reservation.application;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.ApplicationException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.exception.ReservationException;
import roomescape.reservation.presentation.response.ReservationResponse;

@Service
public class ReservationSaveService {

    private final ReservationRepository reservationRepository;

    public ReservationSaveService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public ReservationResponse saveReservation(Reservation reservation) {
        try {
            reservationRepository.save(reservation);
            return ReservationResponse.from(reservation);
        } catch (DataIntegrityViolationException e) {
            throw new ApplicationException(ReservationException.DUPLICATE_RESERVATION_REQUEST);
        }
    }
}
