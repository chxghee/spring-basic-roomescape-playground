package roomescape.reservation.application;

public record ReservationCommand(
        String date,
        Long memberId,
        String name,
        Long theme,
        Long time
) {
}
