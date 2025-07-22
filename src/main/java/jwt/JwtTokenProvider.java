package jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import roomescape.auth.AuthException;
import roomescape.exception.ApplicationException;
import roomescape.member.domain.Member;

import java.util.Date;

public class JwtTokenProvider {

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
                .setIssuedAt(now)
                .setExpiration(expirationTime)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
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
