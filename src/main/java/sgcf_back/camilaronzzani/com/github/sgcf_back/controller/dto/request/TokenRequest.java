package sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request;

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
