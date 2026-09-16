package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Reservation;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.ReservationService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;


public record ReservationRequest (

    @NotEmpty
    @NotBlank
     LocalDate date,

    @NotEmpty
    @NotBlank
     Long tourId,

    @NotEmpty
    @NotBlank
     Long customerId,

    @NotEmpty
    @NotBlank
     Long employeeId,

    @NotEmpty
    @NotBlank
     double value,

    @NotEmpty
    @NotBlank
     Status status
){
    @Autowired
    private static ReservationService reservationService;
    public static Reservation toReservation(ReservationRequest reservationRequest) {

        Reservation reservation = new Reservation();
        reservation.setDate(reservationRequest.date());
        reservation.setTour(reservationService.findTour(reservationRequest.tourId()));
        reservation.setCustomer(reservationService.findCustomer(reservationRequest.customerId()));
        reservation.setEmployee(reservationService.findEmployee(reservationRequest.employeeId()));
        reservation.setValue(reservationRequest.value());
        reservation.setStatus(reservationRequest.status() == null
                ? Status.Pending
                : reservationRequest.status());
        reservation.setActive(true);
        return reservation;
    }
}
