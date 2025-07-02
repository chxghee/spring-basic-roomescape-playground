package roomescape.reservation.presentation.response;

import roomescape.reservation.domain.Reservation;
import roomescape.waiting.domain.WaitingWithRank;

public record MyReservationResponse(
        Long id,
        String theme,
        String date,
        String time,
        String status
) {
    private final static String RESERVATION_STATUS_MESSAGE = "예약";
    private final static String RESERVATION_WAITING_STATUS_MESSAGE = "번째 예약대기";

    public static MyReservationResponse from(Reservation reservation) {
        return new MyReservationResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getValue(),
                RESERVATION_STATUS_MESSAGE
        );
    }

    public static MyReservationResponse from(WaitingWithRank waitingWithRank) {
        return new MyReservationResponse(
                waitingWithRank.getWaiting().getId(),
                waitingWithRank.getWaiting().getTheme().getName(),
                waitingWithRank.getWaiting().getDate(),
                waitingWithRank.getWaiting().getTime().getValue(),
                waitingWithRank.getRank() + RESERVATION_WAITING_STATUS_MESSAGE
        );
    }


}
