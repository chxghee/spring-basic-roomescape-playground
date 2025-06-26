package roomescape.member.application;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import roomescape.exception.ApplicationException;
import roomescape.member.domain.Member;
import roomescape.member.infrastructure.MemberDao;
import roomescape.member.exception.MemberException;
import roomescape.member.domain.Role;
import roomescape.member.presentation.request.LoginRequest;
import roomescape.member.presentation.request.MemberRequest;
import roomescape.member.presentation.response.MemberResponse;

@Service
public class MemberService {

    private final MemberDao memberDao;

    public MemberService(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    public MemberResponse createMember(MemberRequest memberRequest) {
        Member member = memberDao.save(new Member(memberRequest.name(), memberRequest.email(), memberRequest.password(), Role.USER));
        return new MemberResponse(member.getId(), member.getName(), member.getEmail());
    }

    public Member loginMember(LoginRequest loginRequest) {
        return getMemberByLoginRequest(loginRequest);
    }

    private Member getMemberByLoginRequest(LoginRequest loginRequest) {
        try {
            return memberDao.findByEmailAndPassword(loginRequest.email(), loginRequest.password());
        } catch (EmptyResultDataAccessException e) {
            throw new ApplicationException(MemberException.LOGIN_FAILED);
        }
    }
}
