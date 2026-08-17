package ch.schnitzeljagd.hunt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface HuntRepository extends JpaRepository<Hunt, Long> {

    Optional<Hunt> findByActiveTrue();

    List<Hunt> findAllByOrderByCreatedAsc();
}
