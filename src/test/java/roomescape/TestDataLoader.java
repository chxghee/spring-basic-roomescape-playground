package roomescape;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.member.domain.Role;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;
import roomescape.time.domain.Time;
import roomescape.time.domain.TimeRepository;

@Profile("test")
@Component
public class TestDataLoader implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final TimeRepository timeRepository;
    private final ThemeRepository themeRepository;
    private final ReservationRepository reservationRepository;

    public TestDataLoader(MemberRepository memberRepository, TimeRepository timeRepository,
                          ThemeRepository themeRepository, ReservationRepository reservationRepository) {
        this.memberRepository = memberRepository;
        this.timeRepository = timeRepository;
        this.themeRepository = themeRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Member admin = new Member("어드민", "admin@email.com", "password", Role.ADMIN);
        Member user = new Member("브라운", "brown@email.com", "password", Role.USER);
        memberRepository.save(admin);
        memberRepository.save(user);

        Theme theme1 = new Theme("테마1", "테마1입니다.");
        Theme theme2 = new Theme("테마2", "테마2입니다.");
        Theme theme3 = new Theme("테마3", "테마3입니다.");
        themeRepository.save(theme1);
        themeRepository.save(theme2);
        themeRepository.save(theme3);

        Time time1 = new Time("10:00");
        Time time2 = new Time("12:00");
        Time time3 = new Time("14:00");
        Time time4 = new Time("16:00");
        Time time5 = new Time("18:00");
        Time time6 = new Time("20:00");
        timeRepository.save(time1);
        timeRepository.save(time2);
        timeRepository.save(time3);
        timeRepository.save(time4);
        timeRepository.save(time5);
        timeRepository.save(time6);

        Reservation reservation1 = new Reservation(admin, "2024-03-01", time1, theme1);
        Reservation reservation2 = new Reservation(admin, "2024-03-01", time2, theme2);
        Reservation reservation3 = new Reservation(admin, "2024-03-01", time3, theme3);
        Reservation reservation4 = new Reservation("브라운", "2024-03-01", time1, theme2);
        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);
        reservationRepository.save(reservation3);
        reservationRepository.save(reservation4);
    }
}
