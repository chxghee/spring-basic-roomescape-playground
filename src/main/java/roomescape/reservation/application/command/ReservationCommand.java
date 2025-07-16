package roomescape.reservation.application.command;

public record ReservationCommand(
        String date,
        Long memberId,
        String name,
        Long theme,
        Long time
) {
}
