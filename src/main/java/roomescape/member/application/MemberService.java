package roomescape.member.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.ApplicationException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.member.exception.MemberException;
import roomescape.member.domain.Role;
import roomescape.member.presentation.request.LoginRequest;
import roomescape.member.presentation.request.MemberRequest;
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
        Member member = memberRepository.save(new Member(memberRequest.name(), memberRequest.email(), memberRequest.password(), Role.USER));
        return new MemberResponse(member.getId(), member.getName(), member.getEmail());
    }

    public Member loginMember(LoginRequest loginRequest) {
        return getMemberByLoginRequest(loginRequest);
    }

    private Member getMemberByLoginRequest(LoginRequest loginRequest) {
        return memberRepository.findByEmailAndPassword(loginRequest.email(), loginRequest.password())
                .orElseThrow(() -> new ApplicationException(MemberException.LOGIN_FAILED));
    }
}
