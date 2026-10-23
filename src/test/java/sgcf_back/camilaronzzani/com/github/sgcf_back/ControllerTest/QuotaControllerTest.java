package sgcf_back.camilaronzzani.com.github.sgcf_back.ControllerTest;

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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class QuotaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private QuotaService quotaService;

    @InjectMocks
    private QuotaController quotaController;

    private ObjectMapper objectMapper;

    private Quota sampleQuota;
    private QuotaDto sampleDto;
    private QuotaRequest sampleRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(quotaController).build();

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
    void findAll_ShouldReturnList() throws Exception {
        when(quotaService.findAll()).thenReturn(List.of(sampleQuota));
        when(quotaService.toDto(sampleQuota)).thenReturn(sampleDto);

        mockMvc.perform(get("/api/quota/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].targetValue").value(1000.0));

        verify(quotaService, times(1)).findAll();
    }

    @Test
    @DisplayName("Buscar meta pelo id")
    void findById_ShouldReturnQuota() throws Exception {
        when(quotaService.findById(1L)).thenReturn(sampleQuota);
        when(quotaService.toDto(sampleQuota)).thenReturn(sampleDto);

        mockMvc.perform(get("/api/quota/findId/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.achievedValue").value(500.0));

        verify(quotaService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Salvar meta")
    void save_ShouldReturnCreated() throws Exception {
        doNothing().when(quotaService).save(any(QuotaRequest.class));

        mockMvc.perform(post("/api/quota/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated());

        verify(quotaService, times(1)).save(any(QuotaRequest.class));
    }

    @Test
    @DisplayName("Atualizar meta")
    void update_ShouldReturnNoContent() throws Exception {
        when(quotaService.update(any(QuotaRequest.class), eq(1L))).thenReturn(sampleQuota);

        mockMvc.perform(put("/api/quota/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isNoContent());

        verify(quotaService, times(1)).update(any(QuotaRequest.class), eq(1L));
    }

    @Test
    @DisplayName("Deletar meta")
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(quotaService).delete(1L);

        mockMvc.perform(delete("/api/quota/delete/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(quotaService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("Listar metas ativas")
    void findAllActive_ShouldReturnList() throws Exception {
        when(quotaService.findAllActive()).thenReturn(List.of(sampleQuota));
        when(quotaService.toDto(sampleQuota)).thenReturn(sampleDto);

        mockMvc.perform(get("/api/quota/findAll/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(quotaService, times(1)).findAllActive();
    }

    @Test
    @DisplayName("Buscar metas do funcionário")
    void findByEmployee_ShouldReturnList() throws Exception {
        when(quotaService.findByEmployee(1L)).thenReturn(List.of(sampleQuota));
        when(quotaService.toDto(sampleQuota)).thenReturn(sampleDto);

        mockMvc.perform(get("/api/quota/findByEmployee/{employeeId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(quotaService, times(1)).findByEmployee(1L);
    }

    @Test
    @DisplayName("Listar metas da empresa")
    void findAllCompany_ShouldReturnList() throws Exception {
        when(quotaService.findAllCompany()).thenReturn(List.of(sampleQuota));
        when(quotaService.toDto(sampleQuota)).thenReturn(sampleDto);

        mockMvc.perform(get("/api/quota/findAll/company"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(quotaService, times(1)).findAllCompany();
    }
}
