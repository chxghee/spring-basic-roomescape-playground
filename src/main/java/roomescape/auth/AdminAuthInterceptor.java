package roomescape.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.common.CookieUtil;
import roomescape.exception.ApplicationException;
import roomescape.member.Role;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    public AdminAuthInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String accessToken = CookieUtil.extractToken(request.getCookies());
        LoginMember loginMember = jwtTokenProvider.getLoginMember(accessToken);
        if (Role.ADMIN.equals(loginMember.role())) {
            return true;
        }
        throw new ApplicationException(AuthException.FORBIDDEN_ADMIN_ACCESS);
    }
}
