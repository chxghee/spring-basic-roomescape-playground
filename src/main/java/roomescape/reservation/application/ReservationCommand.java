package roomescape.reservation.application;

public record ReservationCommand(
        String date,
        String name,
        Long theme,
        Long time
) {
}
