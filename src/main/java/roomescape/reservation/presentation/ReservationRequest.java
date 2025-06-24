package roomescape.reservation.presentation;

import roomescape.auth.LoginMember;
import roomescape.exception.ApplicationException;
import roomescape.reservation.ReservationException;
import roomescape.reservation.application.ReservationCommand;

public record ReservationRequest(
        String date,
        String name,
        Long theme,
        Long time
) {

    public ReservationCommand toCommand(LoginMember loginMember) {
        validateRequest();
        String reservationMember = getReservationMember(loginMember);
        return new ReservationCommand(
                date,
                reservationMember,
                theme,
                time
        );
    }

    private void validateRequest() {
        if (date == null || theme == null || time == null) {
            throw new ApplicationException(ReservationException.INVALID_REQUEST);
        }
    }

    private String getReservationMember(LoginMember loginMember) {
        String reservationMember = loginMember.name();
        if (name != null) {
            reservationMember = name;
        }
        return reservationMember;
    }
}
