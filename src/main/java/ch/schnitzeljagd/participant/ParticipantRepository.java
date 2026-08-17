package ch.schnitzeljagd.participant;

import ch.schnitzeljagd.hunt.Hunt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Optional<Participant> findByCode(String code);

    boolean existsByCode(String code);

    List<Participant> findAllByOrderByStartDesc();

    @Query("SELECT p FROM Participant p WHERE p.duration IS NOT NULL AND p.hunt = :hunt ORDER BY p.duration ASC")
    List<Participant> findRankingByHunt(Hunt hunt);
}
