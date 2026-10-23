package sgcf_back.camilaronzzani.com.github.sgcf_back.controllerTest;

import sgcf_back.camilaronzzani.com.github.sgcf_back.config.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.PasswordResetController;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.BooleanRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.PasswordResetRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.TokenRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.PasswordResetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PasswordResetController.class)
@Import(SecurityConfig.class)
class PasswordResetTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PasswordResetService passwordResetService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Pedir troca de senha")
    void requestPasswordResetReturnsMessage() throws Exception {
        PasswordResetRequest request = new PasswordResetRequest("camila@email.com");
        when(passwordResetService.requestPasswordReset(any(PasswordResetRequest.class)))
                .thenReturn("Email sent");

        ResultActions resposta = mockMvc.perform(post("/api/password/reset/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email sent"));
        verify(passwordResetService, times(1)).requestPasswordReset(any(PasswordResetRequest.class));
    }

    @Test
    @DisplayName("Token válido")
    void compareTokenReturnsTrueWhenTokenIsValid() throws Exception {
        TokenRequest request = new TokenRequest("123456", "camila@email.com");
        when(passwordResetService.compareToken(any(TokenRequest.class)))
                .thenReturn(new BooleanRequest(true, "Valid token"));

        ResultActions resposta = mockMvc.perform(post("/api/password/reset/compareToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.bool").value(true))
                .andExpect(jsonPath("$.message").value("Valid token"));
        verify(passwordResetService, times(1)).compareToken(any(TokenRequest.class));
    }

    @Test
    @DisplayName("Token inválido")
    void compareTokenReturnsFalseWhenTokenIsInvalid() throws Exception {
        TokenRequest request = new TokenRequest("000000", "camila@email.com");
        when(passwordResetService.compareToken(any(TokenRequest.class)))
                .thenReturn(new BooleanRequest(false, "Invalid token"));

        ResultActions resposta = mockMvc.perform(post("/api/password/reset/compareToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.bool").value(false));
        verify(passwordResetService, times(1)).compareToken(any(TokenRequest.class));
    }
}
