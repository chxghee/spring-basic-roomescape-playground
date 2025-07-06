package roomescape.waiting.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "waiting_order_counter", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"theme_id", "date", "time_id"})
})
public class WaitingOrderCounter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "theme_id", nullable = false)
    private Long themeId;

    @Column(nullable = false)
    private String date;

    @Column(name = "time_id", nullable = false)
    private Long timeId;

    private Long lastOrder;

    public WaitingOrderCounter(Long themeId, String date, Long timeId, Long lastOrder) {
        this.themeId = themeId;
        this.date = date;
        this.timeId = timeId;
        this.lastOrder = lastOrder;
    }

    protected WaitingOrderCounter() {}

    public void increaseOrder() {
        this.lastOrder++;
    }

    public void decreaseOrder() {
        if (this.lastOrder > 0) {
            this.lastOrder--;
        }
    }

    public Long getId() {
        return id;
    }

    public Long getThemeId() {
        return themeId;
    }

    public String getDate() {
        return date;
    }

    public Long getTimeId() {
        return timeId;
    }

    public Long getLastOrder() {
        return lastOrder;
    }
}
