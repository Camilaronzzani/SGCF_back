package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRequest {

    @NotEmpty
    @NotBlank
    private String token ;

    @NotEmpty
    @NotBlank
    private long idPasswordReset;
}
