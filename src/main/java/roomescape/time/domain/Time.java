package roomescape.time.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "time")
public class Time {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String value;

    @Column(nullable = false)
    private boolean deleted = false;

    public Time(Long id, String value) {
        this.id = id;
        this.value = value;
    }

    public Time(String value) {
        this.value = value;
    }

    protected Time() {
    }

    public void delete(){
        this.deleted = true;
    }

    public Long getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
