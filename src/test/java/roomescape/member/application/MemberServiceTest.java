package roomescape.member.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.ApplicationException;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.exception.MemberException;
import roomescape.member.domain.MemberRepository;
import roomescape.member.presentation.request.LoginRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(new Member("changhee", "asd@email.com", "1234", Role.USER));
    }

    @Test
    void 로그인에_성공하면_로그인한_사용자_Member객체를_반환해야_한다() {
        Member loginMember = memberService.loginMember(new LoginRequest("asd@email.com", "1234"));
        assertThat(loginMember.getId()).isEqualTo(member.getId());
    }

    @Test
    void 로그인에_실패하면_예외가_발생해야_한다() {
        assertThatThrownBy(() -> memberService.loginMember(new LoginRequest("asd@email.com", "asd")))
                .isInstanceOf(ApplicationException.class)
                .extracting("code")
                .isEqualTo(MemberException.LOGIN_FAILED);
    }
}
