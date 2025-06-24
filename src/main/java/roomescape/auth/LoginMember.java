package roomescape.auth;

import roomescape.member.Role;

public record LoginMember(
        Long id,
        String name,
        Role role
) {
}
