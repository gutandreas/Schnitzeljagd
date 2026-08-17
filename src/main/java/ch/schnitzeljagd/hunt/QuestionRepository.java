package ch.schnitzeljagd.hunt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByHuntOrderByPositionAsc(Hunt hunt);

    Optional<Question> findByToken(String token);

    boolean existsByToken(String token);

    long countByHunt(Hunt hunt);

    void deleteByHunt(Hunt hunt);
}
