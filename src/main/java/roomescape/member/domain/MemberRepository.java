package roomescape.member.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Member.class, id));
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
}
