package roomescape.member.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.exception.ApplicationException;
import roomescape.member.exception.MemberException;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmailAndPassword(String email, String password);

    default Member getMemberByEmailAndPassword(String email, String password) {
        return findByEmailAndPassword(email, password)
                .orElseThrow(() -> new ApplicationException(MemberException.LOGIN_FAILED));
    }

    default Member getMemberById(Long memberId) {
        return findById(memberId)
                .orElseThrow(() -> new ApplicationException(MemberException.MEMBER_NOT_FOUND));
    }
}
