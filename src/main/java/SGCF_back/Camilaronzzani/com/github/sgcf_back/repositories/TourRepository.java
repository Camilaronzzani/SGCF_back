package sgcf_back.camilaronzzani.com.github.sgcf_back.repositories;

import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TourRepository extends JpaRepository<Tour, Long> {
    List<Tour> findByActiveTrue();
}
