package roomescape.reservation.presentation.response;

public record ReservationResponse(
        Long id,
        String name,
        String theme,
        String date,
        String time
) {
}
