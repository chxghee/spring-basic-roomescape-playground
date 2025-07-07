package roomescape.theme.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "theme")
public class Theme {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @Column(nullable = false)
    private boolean deleted = false;

    protected Theme() {
    }

    public Theme(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void delete() {
        this.deleted = true;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
