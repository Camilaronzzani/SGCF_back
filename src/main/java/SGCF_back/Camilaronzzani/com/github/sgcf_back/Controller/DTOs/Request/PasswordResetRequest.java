package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PasswordResetRequest {
    private long userId;
    private LocalDateTime StartTime;
    private String email;

}
