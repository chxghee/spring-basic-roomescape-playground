package roomescape.reservation.presentation.request;

import roomescape.auth.LoginMember;
import roomescape.exception.ApplicationException;
import roomescape.reservation.exception.ReservationException;
import roomescape.reservation.application.command.ReservationCommand;

import java.util.ArrayList;
import java.util.List;

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
        List<String> errorFields = new ArrayList<>();

        if (date == null) {
            errorFields.add("날짜");
        }
        if (theme == null) {
            errorFields.add("테마");
        }
        if (time == null) {
            errorFields.add("시간");
        }

        if (!errorFields.isEmpty()) {
            throw new ApplicationException(ReservationException.INVALID_REQUEST, errorFields);
        }
    }
}
