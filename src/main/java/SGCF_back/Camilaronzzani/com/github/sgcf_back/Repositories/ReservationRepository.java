package SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByActiveTrue();

    long countByTour_IdAndActiveTrue(Long tourId);

    List<Reservation> findByCustomerId(Long customerId);

    List<Reservation> findByEmployeeId(Long employeeId);

    List<Reservation> findByStatus(Status status);
}
