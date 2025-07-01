package roomescape.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.common.CookieUtil;
import roomescape.exception.ApplicationException;
import roomescape.member.domain.Member;
import roomescape.member.infrastructure.MemberDao;

import java.util.Map;
import java.util.Set;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private static final Map<String, Set<String>> WHITE_LIST = Map.of(
            "/times", Set.of("GET"),
            "/themes", Set.of("GET")
    );
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberDao memberDao;


    public AdminAuthInterceptor(JwtTokenProvider jwtTokenProvider, MemberDao memberDao) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.memberDao = memberDao;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (isWhiteListed(request)) {
           return true;
        }

        LoginMember loginMember = getLoginMemberFromAccessToken(request);

        if (loginMember.isAdmin()) {
            request.setAttribute("loginMember", loginMember);
            return true;
        }
        throw new ApplicationException(AuthException.FORBIDDEN_ADMIN_ACCESS);
    }

    private LoginMember getLoginMemberFromAccessToken(HttpServletRequest request) {
        String accessToken = CookieUtil.extractToken(request.getCookies());
        Long loginMemberId = jwtTokenProvider.getLoginMemberId(accessToken);
        Member member = memberDao.findById(loginMemberId)
                .orElseThrow(() -> new ApplicationException(AuthException.INVALID_USER_ID));
        return LoginMember.from(member);
    }

    private static boolean isWhiteListed(HttpServletRequest request) {
        return WHITE_LIST.entrySet().stream()
                .anyMatch(entry ->
                        request.getRequestURI().startsWith(entry.getKey()) &&
                                entry.getValue().contains(request.getMethod()));
    }
}
