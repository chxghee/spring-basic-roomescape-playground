package roomescape.reservation.application;

import roomescape.member.domain.Role;

public record ReservationCommand(
        String date,
        Long memberId,
        String name,
        Long theme,
        Long time,
        Role role
) {
    public boolean isAdmin() {
        return role.equals(Role.ADMIN);
    }
}
