package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PasswordResetRequest {
    @NotBlank
    @NotEmpty
    @Email
    private String email;

}
