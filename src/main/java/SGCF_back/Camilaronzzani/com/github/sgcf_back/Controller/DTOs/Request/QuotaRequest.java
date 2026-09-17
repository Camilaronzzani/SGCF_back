package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import jakarta.validation.constraints.Positive;

import java.time.LocalDate;


public record QuotaRequest (

    LocalDate startDate,

     LocalDate endDate,

    @Positive
     double targetValue,

    @Positive
     Long employeeId
){
}
