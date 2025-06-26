package roomescape.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.common.CookieUtil;
import roomescape.exception.ApplicationException;

import java.util.Map;
import java.util.Set;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private static final Map<String, Set<String>> WHITE_LIST = Map.of(
            "/times", Set.of("GET"),
            "/themes", Set.of("GET")
    );
    private final JwtTokenProvider jwtTokenProvider;


    public AdminAuthInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (isWhiteListed(request)) {
           return true;
        }

        String accessToken = CookieUtil.extractToken(request.getCookies());
        LoginMember loginMember = jwtTokenProvider.getLoginMember(accessToken);
        if (loginMember.isAdmin()) {
            return true;
        }
        throw new ApplicationException(AuthException.FORBIDDEN_ADMIN_ACCESS);
    }

    private static boolean isWhiteListed(HttpServletRequest request) {
        return WHITE_LIST.entrySet().stream()
                .anyMatch(entry ->
                        request.getRequestURI().startsWith(entry.getKey()) &&
                                entry.getValue().contains(request.getMethod()));
    }
}
