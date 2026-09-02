package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    @NotBlank(message = "can't be blank")
    @Email
    private String email;
    @NotBlank(message = "the password can't be blank")
    private String password;
}
