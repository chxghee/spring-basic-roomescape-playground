package roomescape.waiting.application.command;

public record WaitingCommand(
        String date,
        Long memberId,
        Long theme,
        Long time
) {
}
