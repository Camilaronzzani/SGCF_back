package sgcf_back.camilaronzzani.com.github.sgcf_back.ControllerTest;

import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.PasswordResetController;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.BooleanRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.PasswordResetRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.TokenRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.PasswordResetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetTest {

    private MockMvc mockMvc;

    @Mock
    private PasswordResetService passwordResetService;

    @InjectMocks
    private PasswordResetController passwordResetController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(passwordResetController).build();
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
