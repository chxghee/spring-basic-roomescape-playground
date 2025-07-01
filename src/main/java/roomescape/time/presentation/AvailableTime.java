package roomescape.time.presentation;

public record AvailableTime(
        Long timeId,
        String time,
        boolean booked
) {
}
