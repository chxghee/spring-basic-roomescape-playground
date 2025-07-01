package roomescape.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.common.CookieUtil;
import roomescape.exception.ApplicationException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    public LoginMemberArgumentResolver(JwtTokenProvider jwtTokenProvider, MemberRepository memberRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.memberRepository = memberRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(LoginMember.class) &&
                parameter.hasParameterAnnotation(AuthenticatedMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        LoginMember loginMember = (LoginMember) request.getAttribute("loginMember");
        if (loginMember != null) {
            return loginMember;
        }

        return getLoginMemberFromAccessToken(request);
    }

    private LoginMember getLoginMemberFromAccessToken(HttpServletRequest request) {
        String accessToken = CookieUtil.extractToken(request.getCookies());
        Long loginMemberId = jwtTokenProvider.getLoginMemberId(accessToken);
        Member member = memberRepository.findById(loginMemberId)
                .orElseThrow(() -> new ApplicationException(AuthException.INVALID_USER_ID));
        return LoginMember.from(member);
    }
}
