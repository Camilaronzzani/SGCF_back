package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class QuotaRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private double targetValue;
    private Long employeeId;
}
