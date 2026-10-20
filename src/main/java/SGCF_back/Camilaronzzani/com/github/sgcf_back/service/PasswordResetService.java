package sgcf_back.camilaronzzani.com.github.sgcf_back.service;

import Functions.GenerateCode;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.BooleanRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.PasswordResetRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.TokenRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.PasswordReset;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.User;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.PasswordResetRepository;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.UserRepository;
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

            log.info("[{}]",passwordResetRequest.email());

            PasswordReset passwordReset = toPasswordReset(passwordResetRequest);
            passwordReset.setToken(code);

            emailService.sendEmail(passwordResetRequest.email(), code);

            passwordResetRepository.save(passwordReset);

            log.info("Request Password does successfully");
            return "Email enviado com sucesso ao email: " + passwordResetRequest.email() ;

        } catch (Exception e) {
            log.error("Erro in PasswordResetService.requestPasswordReset" , e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid  reset token request");
        }
    }
    private PasswordReset toPasswordReset(PasswordResetRequest passwordResetRequest){
        try {

            PasswordReset passwordReset = new PasswordReset();
            passwordReset.setExpiration(LocalDateTime.now().plusMinutes(10));

            User user = userRepository.findByEmail(passwordResetRequest.email()).orElseThrow(()
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
            String message = "";


            User user = userRepository.findByEmail(tokenRequest.email()).orElseThrow(()
                    ->  new ResponseStatusException(HttpStatus.NOT_FOUND, "user no find"));

            PasswordReset passwordReset = passwordResetRepository.findFirstByUserIdAndUsedFalseOrderByExpirationDesc(user.getId())
                    .orElseThrow(() ->new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found") );

            if (!passwordReset.getToken().equals(tokenRequest.token())){
               message = "Token incorreto";
            }else if (!passwordReset.getExpiration().isAfter(LocalDateTime.now())){
               message = "Tempo expirado" ;
            }

            boolean  boo= (passwordReset.getToken().equals(tokenRequest.token().trim()) && passwordReset.getExpiration().isAfter(LocalDateTime.now()));
            log.info("Validação do token para {}: {}", tokenRequest.email(), boo);

            BooleanRequest booleanRequest = new BooleanRequest(boo, message);
            return  booleanRequest;

        } catch (Exception e) {
            log.error("Erro in PasswordResetService.compareToken" , e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired reset token request");
        }
    }
}
