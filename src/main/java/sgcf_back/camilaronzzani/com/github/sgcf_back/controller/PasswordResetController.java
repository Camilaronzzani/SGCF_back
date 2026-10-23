package sgcf_back.camilaronzzani.com.github.sgcf_back.controller;

import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.BooleanRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.PasswordResetRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.TokenRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.PasswordResetService;
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
