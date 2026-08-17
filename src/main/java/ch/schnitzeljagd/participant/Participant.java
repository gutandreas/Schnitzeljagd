package ch.schnitzeljagd.participant;

import ch.schnitzeljagd.hunt.Hunt;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Eine teilnehmende Person mit ihrem persönlichen Code und ihrer eigenen
 * Postenreihenfolge (damit sich nicht alle am selben Posten drängen).
 */
@Entity
@Table(name = "participants")
public class Participant {

    /** Was ein angeforderter Tipp an Zeit kostet. */
    public static final Duration HINT_PENALTY = Duration.ofMinutes(1);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 8)
    private String code;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @ManyToOne(optional = false)
    @JoinColumn(name = "hunt_id", nullable = false)
    private Hunt hunt;

    /** Die IDs der Posten in der persönlichen Reihenfolge. */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "participant_route", joinColumns = @JoinColumn(name = "participant_id"))
    @OrderColumn(name = "step")
    @Column(name = "question_id")
    private List<Long> route = new ArrayList<>();

    /** Wie viele Posten bereits gelöst sind — zugleich der Index des nächsten. */
    @Column(nullable = false)
    private int solved;

    /**
     * Posten, für die ein Tipp angefordert wurde. Als Menge, damit derselbe Tipp
     * nur einmal kostet — wer die Seite neu lädt, zahlt nicht doppelt.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "participant_hints", joinColumns = @JoinColumn(name = "participant_id"))
    @Column(name = "question_id")
    private Set<Long> hintedQuestions = new HashSet<>();

    @Column(nullable = false)
    private LocalDateTime start;

    private LocalDateTime stop;

    private Duration duration;

    protected Participant() {
        // für JPA
    }

    public Participant(String code, String firstName, String lastName, Hunt hunt, List<Long> route) {
        this.code = code;
        this.firstName = firstName;
        this.lastName = lastName;
        this.hunt = hunt;
        this.route = new ArrayList<>(route);
        this.solved = 0;
        this.start = LocalDateTime.now();
    }

    /** Der Posten, der als nächstes zu lösen ist — {@code null}, wenn alle gelöst sind. */
    public Long getCurrentQuestionId() {
        return solved < route.size() ? route.get(solved) : null;
    }

    /** Der Posten nach dem aktuellen — {@code null}, wenn der aktuelle der letzte ist. */
    public Long getNextQuestionId() {
        return solved + 1 < route.size() ? route.get(solved + 1) : null;
    }

    public void markSolved() {
        if (solved < route.size()) {
            solved++;
        }
    }

    public boolean hasSolvedEverything() {
        return solved >= route.size();
    }

    public boolean isFinished() {
        return stop != null;
    }

    /**
     * Merkt, dass für diesen Posten ein Tipp bezogen wurde.
     *
     * @return true, wenn der Tipp neu ist — nur dann kostet er Zeit.
     */
    public boolean requestHint(Long questionId) {
        return hintedQuestions.add(questionId);
    }

    public boolean hasRequestedHint(Long questionId) {
        return hintedQuestions.contains(questionId);
    }

    public int getHintCount() {
        return hintedQuestions.size();
    }

    /** Der Zeitzuschlag für alle bezogenen Tipps. */
    public Duration getPenalty() {
        return HINT_PENALTY.multipliedBy(hintedQuestions.size());
    }

    /**
     * Stoppt die Zeit; mehrfaches Aufrufen ändert das Ergebnis nicht.
     * Die Tippzuschläge werden dabei aufgerechnet.
     */
    public void finish() {
        if (stop == null) {
            stop = LocalDateTime.now();
            duration = Duration.between(start, stop).plus(getPenalty());
        }
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Hunt getHunt() {
        return hunt;
    }

    public List<Long> getRoute() {
        return route;
    }

    public int getSolved() {
        return solved;
    }

    public int getTotal() {
        return route.size();
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getStop() {
        return stop;
    }

    public Duration getDuration() {
        return duration;
    }

    /** Die Laufzeit als h:mm:ss — leer, solange die Jagd nicht abgeschlossen ist. */
    public String getDurationAsFormattedString() {
        if (duration == null) {
            return "";
        }
        long seconds = duration.getSeconds();
        return String.format("%d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
    }

    @Override
    public String toString() {
        return "Participant[id=" + id + ", code=" + code + ", name=" + firstName + " " + lastName
                + ", solved=" + solved + "/" + route.size() + "]";
    }
}
