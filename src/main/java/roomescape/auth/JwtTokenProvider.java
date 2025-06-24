package roomescape.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import roomescape.exception.ApplicationException;
import roomescape.member.Member;
import roomescape.member.Role;

import java.util.Date;

@Component
public class JwtTokenProvider {

    private final String secretKey;
    private final int expiration;

    public JwtTokenProvider(
            @Value("${roomescape.auth.jwt.secret}") String secretKey,
            @Value("${roomescape.auth.jwt.expiration}") int expiration
    ) {
        this.secretKey = secretKey;
        this.expiration = expiration;
    }

    public String createAccessToken(Member member) {
        Date now = new Date();
        Date expirationTime = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(member.getId().toString())
                .claim("name", member.getName())
                .claim("role", member.getRole())
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
                .get("name").toString();
    }

    public Role getLoginMemberRole(String accessToken) {
        return Role.from(
                getClaims(accessToken)
                        .get("role").toString()
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
            throw new ApplicationException(AuthException.ACCESS_TOKEN_NOT_FOUND);
        } catch (JwtException e) {
            throw new ApplicationException(AuthException.INVALID_ACCESS_TOKEN);
        }
    }
}
