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
        return getReservationMemberCommand(loginMember);
    }

    private void validateRequest() {
        if (date == null || theme == null || time == null) {
            throw new ApplicationException(ReservationException.INVALID_REQUEST);
        }
    }

    private ReservationCommand getReservationMemberCommand(LoginMember loginMember) {
        if (loginMember.isAdmin()) {
            return new ReservationCommand(
                    date,
                    null,
                    name,
                    theme,
                    time,
                    loginMember.role()
            );
        }
        return new ReservationCommand(
                date,
                loginMember.id(),
                loginMember.name(),
                theme,
                time,
                loginMember.role()
        );
    }
}
