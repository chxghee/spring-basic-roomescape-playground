package roomescape;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.member.domain.Role;

@Profile("prod")
@Component
public class DataLoader implements CommandLineRunner {

    private final MemberRepository memberRepository;

    public DataLoader(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Member admin = new Member("어드민", "admin@email.com", "password", Role.ADMIN);
        Member user = new Member("브라운", "brown@email.com", "password", Role.USER);
        memberRepository.save(admin);
        memberRepository.save(user);
    }
}
