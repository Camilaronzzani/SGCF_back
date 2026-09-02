package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {

    @NotEmpty
    @NotBlank
    private Long customerId;

    @NotEmpty
    @NotBlank
    private Status status;

    @NotEmpty
    @NotBlank
    private double totalAccount;
}
