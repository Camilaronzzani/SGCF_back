package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    private String email;
    private String password;
}
