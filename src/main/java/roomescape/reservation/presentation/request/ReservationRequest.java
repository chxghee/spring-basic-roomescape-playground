package roomescape.reservation.presentation.request;

import roomescape.auth.LoginMember;
import roomescape.exception.ApplicationException;
import roomescape.reservation.exception.ReservationException;
import roomescape.reservation.application.ReservationCommand;

public record ReservationRequest(
        String date,
        String name,
        Long theme,
        Long time
) {

    public ReservationCommand toCommand(LoginMember loginMember) {
        validateRequest();
        return new ReservationCommand(date, loginMember.id(), name, theme, time);
    }

    private void validateRequest() {
        if (date == null || theme == null || time == null) {
            throw new ApplicationException(ReservationException.INVALID_REQUEST);
        }
    }
}
