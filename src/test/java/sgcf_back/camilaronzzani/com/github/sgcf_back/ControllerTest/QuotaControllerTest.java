package sgcf_back.camilaronzzani.com.github.sgcf_back.ControllerTest;

import sgcf_back.camilaronzzani.com.github.sgcf_back.config.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.QuotaController;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.QuotaDto;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.QuotaRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Quota;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.QuotaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuotaController.class)
@Import(SecurityConfig.class)
class QuotaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuotaService quotaService;

    private ObjectMapper objectMapper;

    private Quota sampleQuota;
    private QuotaDto sampleDto;
    private QuotaRequest sampleRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        sampleQuota = new Quota();
        sampleQuota.setId(1L);
        sampleQuota.setTargetValue(1000.0);
        sampleQuota.setActive(true);

        sampleDto = new QuotaDto(1L, null, null, 1000.0, 500.0, null);

        sampleRequest = new QuotaRequest(
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                1000.0,
                1L
        );
    }

    @Test
    @DisplayName("Listar todas as metas")
    void findAllReturnsListOfQuotas() throws Exception {
        when(quotaService.findAll()).thenReturn(List.of(sampleQuota));
        when(quotaService.toDto(sampleQuota)).thenReturn(sampleDto);

        ResultActions resposta = mockMvc.perform(get("/api/quota/findAll"));

        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].targetValue").value(1000.0));
        verify(quotaService, times(1)).findAll();
    }

    @Test
    @DisplayName("Buscar meta pelo id")
    void findByIdReturnsQuotaWhenItExists() throws Exception {
        when(quotaService.findById(1L)).thenReturn(sampleQuota);
        when(quotaService.toDto(sampleQuota)).thenReturn(sampleDto);

        ResultActions resposta = mockMvc.perform(get("/api/quota/findId/{id}", 1L));

        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.achievedValue").value(500.0));
        verify(quotaService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Salvar meta")
    void saveReturns201WhenRequestIsValid() throws Exception {
        doNothing().when(quotaService).save(any(QuotaRequest.class));

        ResultActions resposta = mockMvc.perform(post("/api/quota/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest)));

        resposta.andExpect(status().isCreated());
        verify(quotaService, times(1)).save(any(QuotaRequest.class));
    }

    @Test
    @DisplayName("Atualizar meta")
    void updateReturns204WhenQuotaIsUpdated() throws Exception {
        when(quotaService.update(any(QuotaRequest.class), eq(1L))).thenReturn(sampleQuota);

        ResultActions resposta = mockMvc.perform(put("/api/quota/update/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest)));

        resposta.andExpect(status().isNoContent());
        verify(quotaService, times(1)).update(any(QuotaRequest.class), eq(1L));
    }

    @Test
    @DisplayName("Deletar meta")
    void deleteReturns204WhenQuotaIsDeleted() throws Exception {
        doNothing().when(quotaService).delete(1L);

        ResultActions resposta = mockMvc.perform(delete("/api/quota/delete/{id}", 1L));

        resposta.andExpect(status().isNoContent());
        verify(quotaService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("Listar metas ativas")
    void findAllActiveReturnsListOfActiveQuotas() throws Exception {
        when(quotaService.findAllActive()).thenReturn(List.of(sampleQuota));
        when(quotaService.toDto(sampleQuota)).thenReturn(sampleDto);

        ResultActions resposta = mockMvc.perform(get("/api/quota/findAll/active"));

        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(quotaService, times(1)).findAllActive();
    }

    @Test
    @DisplayName("Buscar metas do funcionário")
    void findByEmployeeReturnsEmployeeQuotas() throws Exception {
        when(quotaService.findByEmployee(1L)).thenReturn(List.of(sampleQuota));
        when(quotaService.toDto(sampleQuota)).thenReturn(sampleDto);

        ResultActions resposta = mockMvc.perform(get("/api/quota/findByEmployee/{employeeId}", 1L));

        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(quotaService, times(1)).findByEmployee(1L);
    }

    @Test
    @DisplayName("Listar metas da empresa")
    void findAllCompanyReturnsCompanyQuotas() throws Exception {
        when(quotaService.findAllCompany()).thenReturn(List.of(sampleQuota));
        when(quotaService.toDto(sampleQuota)).thenReturn(sampleDto);

        ResultActions resposta = mockMvc.perform(get("/api/quota/findAll/company"));

        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(quotaService, times(1)).findAllCompany();
    }
}
