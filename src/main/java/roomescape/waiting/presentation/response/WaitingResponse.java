package roomescape.waiting.presentation.response;

import roomescape.waiting.domain.Waiting;

public record WaitingResponse(
        Long id,
        String name,
        String theme,
        String date,
        String time,
        Long waitingNumber
) {
    public static WaitingResponse from(Waiting waiting) {
        return new WaitingResponse(
                waiting.getId(),
                waiting.getMember().getName(),
                waiting.getTheme().getName(),
                waiting.getDate(),
                waiting.getTime().getValue(),
                waiting.getOrder()
        );
    }
}
