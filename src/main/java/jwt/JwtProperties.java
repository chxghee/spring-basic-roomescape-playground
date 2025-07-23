package jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "roomescape.auth.jwt")
public class JwtProperties {

    private final String secret;
    private final int expiration;

    public JwtProperties(String secret, int expiration) {
        this.secret = secret;
        this.expiration = expiration;
    }

    public String getSecret() {
        return secret;
    }

    public int getExpiration() {
        return expiration;
    }
}
