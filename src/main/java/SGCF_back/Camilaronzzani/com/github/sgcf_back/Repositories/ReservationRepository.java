package SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByActiveTrue();

    long countByTour_IdAndActiveTrue(Long tourId);

    List<Reservation> findByCustomerId(Long customerId);

    List<Reservation> findByEmployeeId(Long employeeId);

    long countByEmployeeIdAndActiveTrue(Long employeeId);

    @Query("select coalesce(sum(r.value), 0) from Reservation r where r.employee.id = :employeeId "
        + "and r.active = true and r.status = :status")
    double sumValueByEmployeeAndStatus(@Param("employeeId") Long employeeId, @Param("status") Status status);

    List<Reservation> findByStatus(Status status);

        @Query("select coalesce(sum(r.value), 0) from Reservation r "
            + "where r.active = true and r.status = :status and r.date between :startDate and :endDate")
        double sumValueByStatusAndDateBetween(
            @Param("status") Status status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
        );

        @Query("select coalesce(sum(r.value), 0) from Reservation r "
            + "where r.active = true and r.status = :status and r.employee.id = :employeeId "
            + "and r.date between :startDate and :endDate")
        double sumValueByEmployeeAndStatusAndDateBetween(
            @Param("employeeId") Long employeeId,
            @Param("status") Status status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
        );
}
