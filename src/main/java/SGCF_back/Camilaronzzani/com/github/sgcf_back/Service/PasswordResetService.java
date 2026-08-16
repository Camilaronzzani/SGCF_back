package SGCF_back.Camilaronzzani.com.github.sgcf_back.Service;

import Functions.GenerateCode;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.PasswordResetRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.TokenRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.PasswordReset;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.User;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.PasswordResetRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class PasswordResetService {
    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public Long requestPasswordReset(PasswordResetRequest passwordResetRequest) {
        try {
            String code = GenerateCode.generateCode();

            PasswordReset passwordReset = toPasswordReset(passwordResetRequest);
            passwordReset.setToken(code);
            passwordResetRepository.save(passwordReset);

            emailService.sendEmail(passwordResetRequest.getEmail(), code);
            return passwordReset.getId() ;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private PasswordReset toPasswordReset(PasswordResetRequest passwordResetRequest){
        try {

            PasswordReset passwordReset = new PasswordReset();
            passwordReset.setExpiration(LocalDateTime.now().plusMinutes(10));
            User user = userRepository.findByEmail(passwordResetRequest.getEmail()).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
            passwordReset.setUser(user);
            return passwordReset;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean compareToken(TokenRequest tokenRequest) {
        try {
            PasswordReset passwordReset = passwordResetRepository.findById(tokenRequest.getIdPasswordReset()).orElseThrow(()
                                ->  new ResponseStatusException(HttpStatus.NOT_FOUND, "user no find"));
            if (passwordReset.getToken().equals(tokenRequest.getToken())){
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
