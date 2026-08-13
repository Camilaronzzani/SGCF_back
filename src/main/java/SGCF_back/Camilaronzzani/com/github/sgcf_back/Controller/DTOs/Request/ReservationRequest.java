package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReservationRequest {
    private LocalDate date;
    private Long tourId;
    private Long customerId;
    private Long employeeId;
    private double value;
    private Status status;
}
