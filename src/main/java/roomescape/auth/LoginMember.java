package roomescape.auth;

import roomescape.member.domain.Role;

public record LoginMember(
        Long id,
        String name,
        Role role
) {
}
