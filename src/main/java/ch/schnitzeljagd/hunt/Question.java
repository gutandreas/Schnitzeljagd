package ch.schnitzeljagd.hunt;

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
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Ein Posten der Schnitzeljagd: Frage, Ort, Tipp und die akzeptierten Antworten.
 * Der {@code token} steckt im QR-Code am Posten und identifiziert die Frage.
 */
@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // EAGER (JPA-Standard bei ManyToOne): die Jagd wird in den Vorlagen mitgelesen,
    // und mit open-in-view=false wäre sie sonst dort nicht mehr nachladbar.
    @ManyToOne(optional = false)
    @JoinColumn(name = "hunt_id", nullable = false)
    private Hunt hunt;

    /** Reihenfolge innerhalb der Jagd; Posten 1 ist immer der Einstieg. */
    @Column(nullable = false)
    private int position;

    @Column(nullable = false, unique = true, length = 16)
    private String token;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 200)
    private String place;

    @Column(nullable = false, length = 2000)
    private String text;

    @Column(length = 1000)
    private String hint;

    /** Alle akzeptierten Schreibweisen; verglichen wird ohne Gross-/Kleinschreibung. */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_answers", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "answer", length = 200)
    private List<String> answers = new ArrayList<>();

    protected Question() {
        // für JPA
    }

    public Question(Hunt hunt, int position, String token, String title, String place, String text, String hint, List<String> answers) {
        this.hunt = hunt;
        this.position = position;
        this.token = token;
        this.title = title;
        this.place = place;
        this.text = text;
        this.hint = hint;
        this.answers = new ArrayList<>(answers);
    }

    /** Prüft eine eingegebene Antwort gegen alle hinterlegten Schreibweisen. */
    public boolean accepts(String answer) {
        if (answer == null) {
            return false;
        }
        String trimmed = answer.trim();
        return answers.stream().anyMatch(a -> a.trim().equalsIgnoreCase(trimmed));
    }

    public Long getId() {
        return id;
    }

    public Hunt getHunt() {
        return hunt;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getToken() {
        return token;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = new ArrayList<>(answers);
    }

    /** Die Antworten als eine Zeile — so werden sie im Adminformular bearbeitet. */
    public String getAnswersAsLine() {
        return String.join("; ", answers);
    }

    @Override
    public String toString() {
        return "Question[id=" + id + ", position=" + position + ", title=" + title + "]";
    }
}
