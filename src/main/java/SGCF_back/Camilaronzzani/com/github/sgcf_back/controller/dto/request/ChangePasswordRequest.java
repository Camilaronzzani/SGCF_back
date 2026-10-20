package sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;



public record ChangePasswordRequest (
    @NotBlank(message = "can't be blank")
    @Email
    String email,
    @NotBlank(message = "the password can't be blank")
    String password
){}
