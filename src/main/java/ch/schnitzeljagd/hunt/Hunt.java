package ch.schnitzeljagd.hunt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Eine Schnitzeljagd — früher „Modus". Genau eine Jagd ist aktiv; ihre Fragen
 * bekommen die Teilnehmenden zu sehen.
 */
@Entity
@Table(name = "hunts")
public class Hunt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private LocalDateTime created;

    protected Hunt() {
        // für JPA
    }

    public Hunt(String name) {
        this.name = name;
        this.created = LocalDateTime.now();
        this.active = false;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    @Override
    public String toString() {
        return "Hunt[id=" + id + ", name=" + name + ", active=" + active + "]";
    }
}
