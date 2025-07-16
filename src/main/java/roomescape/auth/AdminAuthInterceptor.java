package roomescape.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.common.CookieUtil;
import roomescape.exception.ApplicationException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;

import java.util.Map;
import java.util.Set;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private static final Map<String, Set<String>> WHITE_LIST = Map.of(
            "/times", Set.of("GET"),
            "/themes", Set.of("GET")
    );
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;


    public AdminAuthInterceptor(JwtTokenProvider jwtTokenProvider, MemberRepository memberRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.memberRepository = memberRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (isWhiteListed(request)) {
           return true;
        }

        Member loginMember = getLoginMemberFromAccessToken(request);

        if (loginMember.isAdmin()) {
            request.setAttribute("loginMember", new LoginMember(loginMember.getId()));
            return true;
        }
        throw new ApplicationException(AuthException.FORBIDDEN_ADMIN_ACCESS);
    }

    private Member getLoginMemberFromAccessToken(HttpServletRequest request) {
        String accessToken = CookieUtil.extractToken(request.getCookies());
        Long loginMemberId = jwtTokenProvider.getLoginMemberId(accessToken);
        return memberRepository.findById(loginMemberId)
                .orElseThrow(() -> new ApplicationException(AuthException.INVALID_USER_ID));
    }

    private static boolean isWhiteListed(HttpServletRequest request) {
        return WHITE_LIST.entrySet().stream()
                .anyMatch(entry ->
                        request.getRequestURI().startsWith(entry.getKey()) &&
                                entry.getValue().contains(request.getMethod()));
    }
}
