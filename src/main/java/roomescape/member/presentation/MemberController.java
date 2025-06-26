package roomescape.member.presentation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.JwtTokenProvider;
import roomescape.auth.LoginMember;
import roomescape.common.CookieUtil;
import roomescape.member.application.MemberService;
import roomescape.member.presentation.request.LoginRequest;
import roomescape.member.presentation.request.MemberRequest;
import roomescape.member.presentation.response.LoginMemberResponse;
import roomescape.member.presentation.response.MemberResponse;

import java.net.URI;

@RestController
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final int accessTokenExpirationSeconds;

    public MemberController(MemberService memberService,
                            JwtTokenProvider jwtTokenProvider,
                            @Value("${roomescape.auth.jwt.expiration}")int accessTokenExpiration) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.accessTokenExpirationSeconds = accessTokenExpiration / 1000;
    }

    @PostMapping("/members")
    public ResponseEntity<MemberResponse> createMember(@RequestBody MemberRequest memberRequest) {
        MemberResponse member = memberService.createMember(memberRequest);
        return ResponseEntity.created(URI.create("/members/" + member.id())).body(member);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        CookieUtil.setToken("", 0, response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        String accessToken = jwtTokenProvider.createAccessToken(memberService.loginMember(loginRequest));
        CookieUtil.setToken(accessToken, accessTokenExpirationSeconds, response);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/login/check")
    public ResponseEntity<LoginMemberResponse> checkLogin(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = CookieUtil.extractToken(request.getCookies());
        LoginMember loginMember = jwtTokenProvider.getLoginMember(accessToken);
        return ResponseEntity.ok(new LoginMemberResponse(loginMember.name()));
    }

}
