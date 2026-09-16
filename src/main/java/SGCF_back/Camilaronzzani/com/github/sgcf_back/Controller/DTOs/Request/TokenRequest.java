package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record TokenRequest (

    @NotEmpty
    @NotBlank
     String token ,

    @NotEmpty
    @NotBlank
    @Email
     String email
){}
