package SGCF_back.Camilaronzzani.com.github.sgcf_back.Service;

import Functions.GenerateCode;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.BooleanRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.PasswordResetRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.TokenRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.PasswordReset;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.User;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.PasswordResetRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Slf4j
@Service
public class PasswordResetService {
    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public String requestPasswordReset(PasswordResetRequest passwordResetRequest) {
        try {
            String code = GenerateCode.generateCode();

            log.info("[{}]",passwordResetRequest.getEmail());

            PasswordReset passwordReset = toPasswordReset(passwordResetRequest);
            passwordReset.setToken(code);

            emailService.sendEmail(passwordResetRequest.getEmail(), code);

            passwordResetRepository.save(passwordReset);

            log.info("Request Password does successfully");
            return "Email enviado com sucesso ao email: " + passwordResetRequest.getEmail() ;

        } catch (Exception e) {
            log.error("Erro in PasswordResetService.requestPasswordReset" , e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid  reset token request");
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
            log.error("Erro in PasswordResetService.toPasswordReset" , e);
            throw new RuntimeException(e);
        }
    }

    public BooleanRequest compareToken(TokenRequest tokenRequest) {
        try {
            BooleanRequest booleanRequest = new BooleanRequest();

            User user = userRepository.findByEmail(tokenRequest.getEmail()).orElseThrow(()
                    ->  new ResponseStatusException(HttpStatus.NOT_FOUND, "user no find"));

            PasswordReset passwordReset = passwordResetRepository.findFirstByUserIdAndUsedFalseOrderByExpirationDesc(user.getId())
                    .orElseThrow(() ->new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found") );

            if (!passwordReset.getToken().equals(tokenRequest.getToken())){
                booleanRequest.setMessage("Token incorreto");
            }else if (!passwordReset.getExpiration().isAfter(LocalDateTime.now())){
                booleanRequest.setMessage("Tempo expirado");
            }

            booleanRequest.setBool(passwordReset.getToken().equals(tokenRequest.getToken().trim()) && passwordReset.getExpiration().isAfter(LocalDateTime.now()));
            log.info("Validação do token para {}: {}", tokenRequest.getEmail(), booleanRequest.getBool());
            return booleanRequest;

        } catch (Exception e) {
            log.error("Erro in PasswordResetService.compareToken" , e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired reset token request");
        }
    }
}
