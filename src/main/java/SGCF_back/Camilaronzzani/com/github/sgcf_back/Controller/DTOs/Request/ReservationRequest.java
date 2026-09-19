package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Reservation;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.CustomerService;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.ReservationService;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;


public record ReservationRequest (

    @FutureOrPresent
     LocalDate date,

    @Positive
     Long tourId,

    @Positive
     Long customerId,

    @Positive
     Long employeeId,

     Status status,

    List<Long> customerNotPaying
){
}
