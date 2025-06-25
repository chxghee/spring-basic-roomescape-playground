package roomescape.auth;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import roomescape.exception.ApplicationException;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;


class JwtTokenProviderTest {

    private final String SECRET_KEY = "jwtsecretkeyjwtsecretkeyjwtsecretkey";
    private final int EXPIRATION = 1000 * 60 * 30;
    private final Member member = new Member(1L, "changhee", "asd@email.com", Role.USER);

    @Test
    void 토큰이_생성되면_id_name_role_정보가_포합되어야_한다() {
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(SECRET_KEY, EXPIRATION);
        String accessToken = jwtTokenProvider.createAccessToken(member);

        LoginMember loginMember = jwtTokenProvider.getLoginMember(accessToken);

        assertSoftly(soft -> {
            soft.assertThat(loginMember.id()).isEqualTo(member.getId());
            soft.assertThat(loginMember.name()).isEqualTo(member.getName());
            soft.assertThat(loginMember.role()).isEqualTo(member.getRole());
        });
    }

    @Test
    void 토큰이_만료되면_예외가_발생해야_한다() {
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(SECRET_KEY, 0);
        String accessToken = jwtTokenProvider.createAccessToken(member);

        assertThatThrownBy(() -> jwtTokenProvider.getLoginMember(accessToken))
                .isInstanceOf(ApplicationException.class)
                .extracting("code")
                .isEqualTo(AuthException.ACCESS_TOKEN_EXPIRED);

    }
}
