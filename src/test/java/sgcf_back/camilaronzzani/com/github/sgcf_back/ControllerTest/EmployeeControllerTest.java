package sgcf_back.camilaronzzani.com.github.sgcf_back.ControllerTest;

import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.EmployeeController;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.EmployeDto;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.AuthenticateRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.DeactivationRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.EmployeeRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Employee;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Language;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.EmployeeService;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.UserService;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private UserService userService;

    @InjectMocks
    private EmployeeController employeeController;

    private ObjectMapper objectMapper;

    private Employee sampleEmployee;
    private EmployeDto sampleDto;
    private EmployeeRequest sampleRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        sampleEmployee = new Employee();
        sampleEmployee.setId(1L);
        sampleEmployee.setCpf("52998224725");
        sampleEmployee.setName("alex");
        sampleEmployee.setLanguagesSpoken(List.of(Language.Portuguese));
        sampleEmployee.setActive(true);

        sampleDto = EmployeDto.toDto(sampleEmployee);

        sampleRequest = new EmployeeRequest(
                "52998224725",
                "camila",
                List.of(Language.Portuguese),
                null
        );
    }

    @Test
    @DisplayName("Listar todos os funcionários")
    void deveRetornarListaDeFuncionarios() throws Exception {
        // Given
        when(employeeService.findAll()).thenReturn(List.of(sampleEmployee));
        when(employeeService.toSummaryDto(sampleEmployee)).thenReturn(sampleDto);

        // When
        ResultActions resposta = mockMvc.perform(get("/api/employee/findAll"));

        // Then
        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("alex"));
        verify(employeeService, times(1)).findAll();
    }

    @Test
    @DisplayName("Desativar funcionário com senha certa")
    void deveDesativarFuncionarioQuandoSenhaEstiverCerta() throws Exception {
        // Given
        DeactivationRequest request = new DeactivationRequest("camila@email.com", "123456");
        when(userService.authenticate(any(AuthenticateRequest.class))).thenReturn(true);

        // When
        ResultActions resposta = mockMvc.perform(delete("/api/employee/deactivate/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Then
        resposta.andExpect(status().isNoContent());
        verify(employeeService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("Não desativar funcionário com senha errada")
    void deveRetornar401QuandoSenhaEstiverErrada() throws Exception {
        // Given
        DeactivationRequest request = new DeactivationRequest("camila@email.com", "wrong");
        when(userService.authenticate(any(AuthenticateRequest.class))).thenReturn(false);

        // When
        ResultActions resposta = mockMvc.perform(delete("/api/employee/deactivate/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Then
        resposta.andExpect(status().isUnauthorized());
        verify(employeeService, never()).delete(anyLong());
    }

    @Test
    @DisplayName("Buscar funcionário pelo id")
    void deveRetornarFuncionarioPeloId() throws Exception {
        // Given
        when(employeeService.findById(1L)).thenReturn(sampleEmployee);

        // When
        ResultActions resposta = mockMvc.perform(get("/api/employee/findId/{id}", 1L));

        // Then
        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cpf").value("52998224725"));
        verify(employeeService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Salvar funcionário")
    void deveSalvarFuncionarioERetornar201() throws Exception {
        // Given
        doNothing().when(employeeService).save(any(EmployeeRequest.class));

        // When
        ResultActions resposta = mockMvc.perform(post("/api/employee/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest)));

        // Then
        resposta.andExpect(status().isCreated());
        verify(employeeService, times(1)).save(any(EmployeeRequest.class));
    }

    @Test
    @DisplayName("Atualizar funcionário")
    void deveAtualizarFuncionario() throws Exception {
        // Given
        when(employeeService.update(any(EmployeeRequest.class), eq(1L))).thenReturn(sampleEmployee);

        // When
        ResultActions resposta = mockMvc.perform(put("/api/employee/update/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest)));

        // Then
        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
        verify(employeeService, times(1)).update(any(EmployeeRequest.class), eq(1L));
    }

    @Test
    @DisplayName("Listar funcionários ativos")
    void deveRetornarListaDeFuncionariosAtivos() throws Exception {
        // Given
        when(employeeService.findAllActive()).thenReturn(List.of(sampleEmployee));
        when(employeeService.toSummaryDto(sampleEmployee)).thenReturn(sampleDto);

        // When
        ResultActions resposta = mockMvc.perform(get("/api/employee/findAll/active"));

        // Then
        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));
        verify(employeeService, times(1)).findAllActive();
    }
}
