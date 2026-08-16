package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRequest {
    private String token ;
    private long idPasswordReset;
}
