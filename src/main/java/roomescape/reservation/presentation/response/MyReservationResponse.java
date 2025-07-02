package roomescape.reservation.presentation.response;

import roomescape.reservation.domain.Reservation;

public record MyReservationResponse(
        Long reservationId,
        String theme,
        String date,
        String time,
        String status
) {
    public static MyReservationResponse from(Reservation reservation) {
        return new MyReservationResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getValue(),
                getStatus(reservation.getWaitingOrder())
        );
    }

    private static String getStatus(Long waitingOrder) {
        if (waitingOrder.equals(1L)) {
            return "예약";
        }
        return "예약 대기";
    }
}
