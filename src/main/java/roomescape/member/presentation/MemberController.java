package roomescape.member.presentation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.AuthenticatedMember;
import roomescape.auth.JwtProperties;
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
                            JwtProperties JwtProperties) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.accessTokenExpirationSeconds = JwtProperties.getExpiration() / 1000;
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
    public ResponseEntity<LoginMemberResponse> checkLogin(HttpServletRequest request, @AuthenticatedMember LoginMember loginMember) {
        return ResponseEntity.ok(new LoginMemberResponse(loginMember.name()));
    }

}
