package roomescape.waiting.domain;

import jakarta.persistence.*;
import roomescape.member.domain.Member;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.Time;

@Entity
@Table(name = "waiting")
public class Waiting {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_id")
    private Time time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    @Column(name = "waiting_order")
    private Long order;

    protected Waiting() {}

    public Waiting(Member member, String date, Time time, Theme theme, Long order) {
        this.member = member;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.order = order;
    }

    public boolean belongsTo(Long loginMemberId) {
        return member.getId().equals(loginMemberId);
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public String getDate() {
        return date;
    }

    public Time getTime() {
        return time;
    }

    public Theme getTheme() {
        return theme;
    }

    public Long getOrder() {
        return order;
    }
}
