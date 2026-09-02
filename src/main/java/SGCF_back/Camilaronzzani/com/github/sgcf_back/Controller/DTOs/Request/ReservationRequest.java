package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReservationRequest {

    @NotEmpty
    @NotBlank
    private LocalDate date;

    @NotEmpty
    @NotBlank
    private Long tourId;

    @NotEmpty
    @NotBlank
    private Long customerId;

    @NotEmpty
    @NotBlank
    private Long employeeId;

    @NotEmpty
    @NotBlank
    private double value;

    @NotEmpty
    @NotBlank
    private Status status;
}
