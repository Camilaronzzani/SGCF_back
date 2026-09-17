package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;


public record ReservationRequest (

    @NotNull
     LocalDate date,

    @NotNull
    @Positive
     Long tourId,

    @NotNull
    @Positive
     Long customerId,

    @NotNull
    @Positive
     Long employeeId,

    @Positive
     double value,

    @NotNull
     Status status,

    List<Long> customerNotPaying
){
}
