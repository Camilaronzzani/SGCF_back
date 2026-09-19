package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record AuthenticateRequest (
    @NotBlank(message = "can't be blank")
    @Email
    String email,
    @NotBlank(message = "the password can't be blank")
    String password
){}
