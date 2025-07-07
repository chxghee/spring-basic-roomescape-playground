package roomescape.member.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.LoginMember;
import roomescape.exception.ApplicationException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.member.exception.MemberException;
import roomescape.member.domain.Role;
import roomescape.member.presentation.request.LoginRequest;
import roomescape.member.presentation.request.MemberRequest;
import roomescape.member.presentation.response.LoginMemberResponse;
import roomescape.member.presentation.response.MemberResponse;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public MemberResponse createMember(MemberRequest memberRequest) {
        Member signUpMember = new Member(memberRequest.name(), memberRequest.email(), memberRequest.password(), Role.USER);
        memberRepository.save(signUpMember);
        return new MemberResponse(signUpMember.getId(), signUpMember.getName(), signUpMember.getEmail());
    }

    public Member loginMember(LoginRequest loginRequest) {
        return memberRepository.getMemberByEmailAndPassword(loginRequest.email(), loginRequest.password());
    }

    public LoginMemberResponse checkLogin(LoginMember loginMember) {
        Member member = memberRepository.getMemberById(loginMember.id());
        return new LoginMemberResponse(member.getName());
    }
}
