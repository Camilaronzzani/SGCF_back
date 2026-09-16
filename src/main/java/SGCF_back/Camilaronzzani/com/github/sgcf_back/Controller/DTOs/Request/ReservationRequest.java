package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Reservation;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.CustomerService;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.ReservationService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;


public record ReservationRequest (

    @NotEmpty
     LocalDate date,

    @NotEmpty
     Long tourId,

    @NotEmpty
     Long customerId,

    @NotEmpty
     Long employeeId,

    @NotEmpty
    @Positive
     double value,

    @NotEmpty
    @NotBlank
     Status status,

    List<Long> customerNotPaying
){
}
