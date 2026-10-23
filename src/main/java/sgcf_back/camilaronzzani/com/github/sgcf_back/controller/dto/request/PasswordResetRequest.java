package sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request;

import jakarta.validation.constraints.Email;

public record PasswordResetRequest (

    @Email
     String email
){}
