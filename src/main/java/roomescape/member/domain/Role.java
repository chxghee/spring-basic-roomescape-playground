package roomescape.member.domain;

import roomescape.exception.ApplicationException;
import roomescape.member.exception.MemberException;

import java.util.Arrays;

public enum Role {

    USER("회원"),
    ADMIN("관리자");

    private final String roleKR;

    Role(String roleKR) {
        this.roleKR = roleKR;
    }

    public static Role from(String name) {
        return Arrays.stream(values())
                .filter(role -> role.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new ApplicationException(MemberException.INVALID_ROLE));
    }

    public String getRoleKR() {
        return roleKR;
    }
}
