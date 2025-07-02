package roomescape.waiting.presentation.request;

import roomescape.auth.LoginMember;
import roomescape.exception.ApplicationException;
import roomescape.reservation.exception.ReservationException;
import roomescape.waiting.application.command.WaitingCommand;

public record WaitingRequest(
        String date,
        Long theme,
        Long time
) {

    public WaitingCommand toCommand(LoginMember loginMember) {
        validateRequest();
        return new WaitingCommand(
                date,
                loginMember.id(),
                theme,
                time
        );
    }

    private void validateRequest() {
        if (date == null || theme == null || time == null) {
            throw new ApplicationException(ReservationException.INVALID_REQUEST);
        }
    }
}
