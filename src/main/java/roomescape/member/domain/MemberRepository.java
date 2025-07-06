package roomescape.member.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import roomescape.exception.ApplicationException;
import roomescape.member.exception.MemberException;
import roomescape.member.presentation.request.LoginRequest;

import java.util.Optional;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Member.class, id));
    }

    public Member getMemberById(Long memberId) {
        return findById(memberId)
                .orElseThrow(() -> new ApplicationException(MemberException.MEMBER_NOT_FOUND));
    }

    public Member save(Member member) {
        entityManager.persist(member);
        return member;
    }

    public Optional<Member> findByEmailAndPassword(String email, String password) {
         return entityManager.createQuery("select m from Member m where m.email = :email and m.password = :password", Member.class)
                 .setParameter("email", email)
                 .setParameter("password", password)
                 .getResultList()
                 .stream()
                 .findFirst();
    }

    public Member getMemberByEmailAndPassword(String email, String password) {
        return findByEmailAndPassword(email, password)
                .orElseThrow(() -> new ApplicationException(MemberException.LOGIN_FAILED));
    }
}
