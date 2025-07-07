package roomescape.waiting.presentation.request;

import roomescape.auth.LoginMember;
import roomescape.exception.ApplicationException;
import roomescape.reservation.exception.ReservationException;
import roomescape.waiting.application.command.WaitingCommand;

import java.util.ArrayList;
import java.util.List;

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
