package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class QuotaRequest {

    @NotEmpty
    @NotBlank
    private LocalDate startDate;

    @NotEmpty
    @NotBlank
    private LocalDate endDate;

    @NotEmpty
    @NotBlank
    private double targetValue;

    @NotEmpty
    @NotBlank
    private Long employeeId;
}
