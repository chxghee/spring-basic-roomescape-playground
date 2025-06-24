package roomescape.member.presentation;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.application.MemberService;
import roomescape.member.presentation.request.LoginRequest;
import roomescape.member.presentation.request.MemberRequest;
import roomescape.member.presentation.response.MemberResponse;

import java.net.URI;

@RestController
public class MemberController {

    private final MemberService memberService;
    private final int accessTokenExpirationSeconds;

    public MemberController(MemberService memberService,
                            @Value("${roomescape.auth.jwt.expiration}")int accessTokenExpiration) {
        this.memberService = memberService;
        this.accessTokenExpirationSeconds = accessTokenExpiration / 1000;
    }

    @PostMapping("/members")
    public ResponseEntity<MemberResponse> createMember(@RequestBody MemberRequest memberRequest) {
        MemberResponse member = memberService.createMember(memberRequest);
        return ResponseEntity.created(URI.create("/members/" + member.getId())).body(member);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        setCookieToken("", 0, response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        String accessToken = memberService.loginMember(loginRequest);
        setCookieToken(accessToken, accessTokenExpirationSeconds, response);
        return ResponseEntity.ok().build();
    }

    private void setCookieToken(String accessToken, int cookieExpirationSeconds, HttpServletResponse response) {
        Cookie cookie = new Cookie("token", accessToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(cookieExpirationSeconds);
        response.addCookie(cookie);
    }
}
