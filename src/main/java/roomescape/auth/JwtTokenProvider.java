package roomescape.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import roomescape.exception.ApplicationException;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;

import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_NAME = "name";
    private final String secretKey;
    private final int expiration;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        secretKey = jwtProperties.getSecret();
        expiration = jwtProperties.getExpiration();
    }

    public String createAccessToken(Member member) {
        Date now = new Date();
        Date expirationTime = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(member.getId().toString())
                .claim(CLAIM_NAME, member.getName())
                .claim(CLAIM_ROLE, member.getRole())
                .setIssuedAt(now)
                .setExpiration(expirationTime)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public LoginMember getLoginMember(String accessToken) {
        return new LoginMember(
                getLoginMemberId(accessToken),
                getLoginMemberName(accessToken),
                getLoginMemberRole(accessToken)
        );
    }

    public String getLoginMemberName(String accessToken) {
        return getClaims(accessToken)
                .get(CLAIM_NAME).toString();
    }

    public Role getLoginMemberRole(String accessToken) {
        return Role.from(
                getClaims(accessToken)
                        .get(CLAIM_ROLE).toString()
        );
    }

    public Long getLoginMemberId(String accessToken) {
        return Long.valueOf(
                getClaims(accessToken)
                        .getSubject()
        );
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new ApplicationException(AuthException.ACCESS_TOKEN_EXPIRED);
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(AuthException.UNAUTHENTICATED_REQUEST);
        } catch (JwtException e) {
            throw new ApplicationException(AuthException.INVALID_ACCESS_TOKEN);
        }
    }
}
