package roomescape.auth;

import roomescape.member.domain.Member;
import roomescape.member.domain.Role;

public record LoginMember(
        Long id,
        String name,
        Role role
) {
    public static LoginMember from (Member member) {
        return new LoginMember(
                member.getId(), member.getName(), member.getRole()
        );
    }

    public boolean isAdmin() {
        return Role.ADMIN.equals(role);
    }
}
