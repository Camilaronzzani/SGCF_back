package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.AuthResponse;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.BooleanRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.PasswordResetRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.TokenRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password/reset")
public class PasswordResetController {
    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/request")
    public ResponseEntity<String> requestPasswordReset(@Valid @RequestBody PasswordResetRequest passwordResetRequest){
            String mensagem = passwordResetService.requestPasswordReset(passwordResetRequest);
            String jsonResponse = "{\"message\": \"" + mensagem + "\"}";
            return ResponseEntity.ok(jsonResponse);
    }
    @PostMapping("/compareToken")
    public ResponseEntity<BooleanRequest> compareToken(@Valid @RequestBody TokenRequest tokenRequest){
            return ResponseEntity.ok(passwordResetService.compareToken(tokenRequest));

    }
}
