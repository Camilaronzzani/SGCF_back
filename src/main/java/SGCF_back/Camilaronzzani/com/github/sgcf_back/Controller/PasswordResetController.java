package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.PasswordResetRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.TokenRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/Password/Reset")
@CrossOrigin(origins = "http://localhost:4200")
public class PasswordResetController {
    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/request")
    public ResponseEntity<Long> requestPasswordReset(@RequestBody PasswordResetRequest passwordResetRequest){
        try {
            return ResponseEntity.ok(passwordResetService.requestPasswordReset(passwordResetRequest));
        } catch (Exception e) {
            return (ResponseEntity<Long>) ResponseEntity.badRequest();
        }
    }
    @PostMapping("/compareToken")
    public ResponseEntity<Boolean> compareToken(@RequestBody TokenRequest tokenRequest){
        try {
            return ResponseEntity.ok(passwordResetService.compareToken(tokenRequest));
        } catch (Exception e) {
            return (ResponseEntity<Boolean>) ResponseEntity.badRequest();        }
    }
}
