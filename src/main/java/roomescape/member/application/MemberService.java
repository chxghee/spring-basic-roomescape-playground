package roomescape.member.application;

import org.springframework.stereotype.Service;
import roomescape.auth.JwtTokenProvider;
import roomescape.exception.ApplicationException;
import roomescape.member.Member;
import roomescape.member.MemberDao;
import roomescape.member.MemberException;
import roomescape.member.Role;
import roomescape.member.presentation.request.LoginRequest;
import roomescape.member.presentation.request.MemberRequest;
import roomescape.member.presentation.response.MemberResponse;

@Service
public class MemberService {

    private final MemberDao memberDao;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberService(MemberDao memberDao, JwtTokenProvider jwtTokenProvider) {
        this.memberDao = memberDao;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public MemberResponse createMember(MemberRequest memberRequest) {
        Member member = memberDao.save(new Member(memberRequest.getName(), memberRequest.getEmail(), memberRequest.getPassword(), Role.USER));
        return new MemberResponse(member.getId(), member.getName(), member.getEmail());
    }

    public String loginMember(LoginRequest loginRequest) {
        Member loginMember = getMemberByLoginRequest(loginRequest);
        return jwtTokenProvider.createAccessToken(loginMember);
    }

    private Member getMemberByLoginRequest(LoginRequest loginRequest) {
        Member findMember = memberDao.findByEmailAndPassword(loginRequest.email(), loginRequest.password());
        if (findMember == null) {
            throw new ApplicationException(MemberException.LOGIN_FAILED);
        }
        return findMember;
    }
}
