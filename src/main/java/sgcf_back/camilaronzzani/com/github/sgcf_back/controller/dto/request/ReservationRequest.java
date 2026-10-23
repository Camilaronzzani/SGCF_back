package sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request;

import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Status;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;

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
