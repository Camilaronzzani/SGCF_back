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
    void findAll_ShouldReturnList() throws Exception {
        when(employeeService.findAll()).thenReturn(List.of(sampleEmployee));
        when(employeeService.toSummaryDto(sampleEmployee)).thenReturn(sampleDto);

        mockMvc.perform(get("/api/employee/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("camila"));

        verify(employeeService, times(1)).findAll();
    }

    @Test
    @DisplayName("Desativar funcionário com senha certa")
    void deactivate_ShouldReturnNoContent_WhenAuthenticated() throws Exception {
        DeactivationRequest request = new DeactivationRequest("camila@email.com", "123456");
        when(userService.authenticate(any(AuthenticateRequest.class))).thenReturn(true);

        mockMvc.perform(delete("/api/employee/deactivate/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(employeeService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("Não desativar funcionário com senha errada")
    void deactivate_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
        DeactivationRequest request = new DeactivationRequest("camila@email.com", "wrong");
        when(userService.authenticate(any(AuthenticateRequest.class))).thenReturn(false);

        mockMvc.perform(delete("/api/employee/deactivate/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(employeeService, never()).delete(anyLong());
    }

    @Test
    @DisplayName("Buscar funcionário pelo id")
    void findById_ShouldReturnEmployee() throws Exception {
        when(employeeService.findById(1L)).thenReturn(sampleEmployee);

        mockMvc.perform(get("/api/employee/findId/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cpf").value("52998224725"));

        verify(employeeService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Salvar funcionário")
    void save_ShouldReturnCreated() throws Exception {
        doNothing().when(employeeService).save(any(EmployeeRequest.class));

        mockMvc.perform(post("/api/employee/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated());

        verify(employeeService, times(1)).save(any(EmployeeRequest.class));
    }

    @Test
    @DisplayName("Atualizar funcionário")
    void update_ShouldReturnUpdatedEmployee() throws Exception {
        when(employeeService.update(any(EmployeeRequest.class), eq(1L))).thenReturn(sampleEmployee);

        mockMvc.perform(put("/api/employee/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(employeeService, times(1)).update(any(EmployeeRequest.class), eq(1L));
    }

    @Test
    @DisplayName("Listar funcionários ativos")
    void findAllActive_ShouldReturnList() throws Exception {
        when(employeeService.findAllActive()).thenReturn(List.of(sampleEmployee));
        when(employeeService.toSummaryDto(sampleEmployee)).thenReturn(sampleDto);

        mockMvc.perform(get("/api/employee/findAll/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(employeeService, times(1)).findAllActive();
    }
}
