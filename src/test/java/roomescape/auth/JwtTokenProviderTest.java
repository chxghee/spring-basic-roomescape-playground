package roomescape.auth;

import org.junit.jupiter.api.Test;
import roomescape.exception.ApplicationException;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;

import static org.assertj.core.api.Assertions.*;


class JwtTokenProviderTest {

    private final String SECRET_KEY = "jwtsecretkeyjwtsecretkeyjwtsecretkey";
    private final int EXPIRATION = 1000 * 60 * 30;
    private final Member member = new Member(1L, "changhee", "asd@email.com", Role.USER);

    @Test
    void 토큰이_생성되면_id_정보가_포합되어야_한다() {
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(new JwtProperties(SECRET_KEY, EXPIRATION));
        String accessToken = jwtTokenProvider.createAccessToken(member);

        Long loginMemberId = jwtTokenProvider.getLoginMemberId(accessToken);

        assertThat(loginMemberId).isEqualTo(member.getId());
    }

    @Test
    void 토큰이_만료되면_예외가_발생해야_한다() {
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(new JwtProperties(SECRET_KEY, 0));
        String accessToken = jwtTokenProvider.createAccessToken(member);

        assertThatThrownBy(() -> jwtTokenProvider.getLoginMemberId(accessToken))
                .isInstanceOf(ApplicationException.class)
                .extracting("code")
                .isEqualTo(AuthException.ACCESS_TOKEN_EXPIRED);

    }
}
