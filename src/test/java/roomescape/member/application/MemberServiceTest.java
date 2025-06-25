package roomescape.member.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.JwtTokenProvider;
import roomescape.exception.ApplicationException;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.exception.MemberException;
import roomescape.member.infrastructure.MemberDao;
import roomescape.member.presentation.request.LoginRequest;
import roomescape.member.presentation.response.LoginMemberResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class MemberServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    private Member member;

    @BeforeEach
    void setUp() {
        member = memberDao.save(new Member("changhee", "asd@email.com", "1234", Role.USER));
    }

    @Test
    void 로그인에_성공하면_토큰을_발급해야_한다() {
        String accessToken = memberService.loginMember(new LoginRequest("asd@email.com", "1234"));
        assertDoesNotThrow(() -> jwtTokenProvider.getLoginMember(accessToken));
    }

    @Test
    void 로그인에_실패하면_예외가_발생해야_한다() {
        assertThatThrownBy(() -> memberService.loginMember(new LoginRequest("asd@email.com", "asd")))
                .isInstanceOf(ApplicationException.class)
                .extracting("code")
                .isEqualTo(MemberException.LOGIN_FAILED);
    }

    @Test
    void 로그인에_성공하면_토큰에_로그인한_사용자의_이름을_확인할_수_있어야한다() {
        String accessToken = memberService.loginMember(new LoginRequest("asd@email.com", "1234"));

        LoginMemberResponse loginMemberResponse = memberService.checkLoginMember(accessToken);

        assertThat(loginMemberResponse.name()).isEqualTo("changhee");
    }
}
