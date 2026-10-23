package sgcf_back.camilaronzzani.com.github.sgcf_back.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.BooleanRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.PasswordResetRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.TokenRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.PasswordReset;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.User;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.PasswordResetRepository;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetTest {

    @Mock
    private PasswordResetRepository passwordResetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PasswordResetService service;

    @Test
    void deveSolicitarRedefinicaoDeSenha() throws Exception {
        PasswordResetRequest request = mock(PasswordResetRequest.class);
        when(request.email()).thenReturn("cliente@teste.com");
        when(userRepository.findByEmail("cliente@teste.com")).thenReturn(Optional.of(new User()));

        String resultado = service.requestPasswordReset(request);

        assertTrue(resultado.contains("cliente@teste.com"));
        verify(emailService).sendEmail(anyString(), anyString());
        verify(passwordResetRepository).save(any(PasswordReset.class));
    }

    @Test
    void deveLancarErroQuandoUsuarioNaoExiste() {
        PasswordResetRequest request = mock(PasswordResetRequest.class);
        when(request.email()).thenReturn("cliente@teste.com");
        when(userRepository.findByEmail("cliente@teste.com")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.requestPasswordReset(request));
    }

    @Test
    void deveValidarTokenCorreto() {
        User user = new User();
        user.setId(1L);

        PasswordReset reset = new PasswordReset();
        reset.setToken("123456");
        reset.setExpiration(LocalDateTime.now().plusMinutes(5));

        TokenRequest request = mock(TokenRequest.class);
        when(request.email()).thenReturn("cliente@teste.com");
        when(request.token()).thenReturn("123456");

        when(userRepository.findByEmail("cliente@teste.com")).thenReturn(Optional.of(user));
        when(passwordResetRepository.findFirstByUserIdAndUsedFalseOrderByExpirationDesc(1L))
                .thenReturn(Optional.of(reset));

        BooleanRequest resultado = service.compareToken(request);

        assertTrue(resultado.bool());
    }

    @Test
    void deveRejeitarTokenIncorreto() {
        User user = new User();
        user.setId(1L);

        PasswordReset reset = new PasswordReset();
        reset.setToken("123456");
        reset.setExpiration(LocalDateTime.now().plusMinutes(5));

        TokenRequest request = mock(TokenRequest.class);
        when(request.email()).thenReturn("cliente@teste.com");
        when(request.token()).thenReturn("000000");

        when(userRepository.findByEmail("cliente@teste.com")).thenReturn(Optional.of(user));
        when(passwordResetRepository.findFirstByUserIdAndUsedFalseOrderByExpirationDesc(1L))
                .thenReturn(Optional.of(reset));

        BooleanRequest resultado = service.compareToken(request);

        assertFalse(resultado.bool());
        assertEquals("Token incorreto", resultado.message());
    }
}