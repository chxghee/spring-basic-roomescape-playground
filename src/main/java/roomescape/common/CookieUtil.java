package roomescape.common;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import roomescape.auth.AuthException;
import roomescape.exception.ApplicationException;

@Component
public class CookieUtil {

    public static void setToken(String accessToken, int cookieExpirationSeconds, HttpServletResponse response) {
        Cookie cookie = new Cookie("token", accessToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(cookieExpirationSeconds);
        response.addCookie(cookie);
    }

    public static String extractToken(Cookie[] cookies) {
        if (cookies == null) {
            throw new ApplicationException(AuthException.UNAUTHENTICATED_REQUEST);
        }
        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        throw new ApplicationException(AuthException.UNAUTHENTICATED_REQUEST);
    }
}
