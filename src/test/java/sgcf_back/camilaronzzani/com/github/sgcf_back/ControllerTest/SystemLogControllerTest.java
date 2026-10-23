package sgcf_back.camilaronzzani.com.github.sgcf_back.ControllerTest;

import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.SystemLogController;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Permission;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.SystemLog;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.SystemLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SystemLogControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SystemLogService systemLogService;

    @InjectMocks
    private SystemLogController systemLogController;

    private SystemLog sampleLog;
    private MockHttpSession managerSession;
    private MockHttpSession unauthorizedSession;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(systemLogController).build();

        sampleLog = new SystemLog();
        sampleLog.setId(1L);
        sampleLog.setLevel("ERROR");
        sampleLog.setLoggerName("com.github.sgcf_back.Service");
        sampleLog.setMessage("Internal server error occurred");
        sampleLog.setExceptionType("NullPointerException");
        sampleLog.setStackTrace("java.lang.NullPointerException...");
        sampleLog.setCreatedAt(LocalDateTime.now());

        managerSession = new MockHttpSession();
        managerSession.setAttribute("userId", 1L);
        managerSession.setAttribute("permission", Permission.Manager);

        unauthorizedSession = new MockHttpSession();
        unauthorizedSession.setAttribute("userId", 2L);
        unauthorizedSession.setAttribute("permission", Permission.Employee);
    }


    @Test
    @DisplayName("GET /api/log/findAll - Without session should return 403 Forbidden")
    void findAllReturns403WhenThereIsNoSession() throws Exception {
        ResultActions resposta = mockMvc.perform(get("/api/log/findAll"));

        resposta.andExpect(status().isForbidden());
        verifyNoInteractions(systemLogService);
    }

    @Test
    @DisplayName("GET /api/log/findAll - Non-manager session should return 403 Forbidden")
    void findAllReturns403WhenUserIsNotManager() throws Exception {
        ResultActions resposta = mockMvc.perform(get("/api/log/findAll").session(unauthorizedSession));

        resposta.andExpect(status().isForbidden());
        verifyNoInteractions(systemLogService);
    }


    @Test
    @DisplayName("GET /api/log/findAll - Manager session should return list of log DTOs")
    void findAllReturnsListOfLogsWhenUserIsManager() throws Exception {
        when(systemLogService.findAll("ERROR")).thenReturn(List.of(sampleLog));

        ResultActions resposta = mockMvc.perform(get("/api/log/findAll")
                .param("level", "ERROR")
                .session(managerSession));

        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].level").value("ERROR"))
                .andExpect(jsonPath("$[0].message").value("Internal server error occurred"));
        verify(systemLogService, times(1)).findAll("ERROR");
    }

    @Test
    @DisplayName("GET /api/log/findId/{id} - Manager session should return log DTO by ID")
    void findByIdReturnsLogWhenUserIsManager() throws Exception {
        when(systemLogService.findById(1L)).thenReturn(sampleLog);

        ResultActions resposta = mockMvc.perform(get("/api/log/findId/{id}", 1L).session(managerSession));

        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.level").value("ERROR"));
        verify(systemLogService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("GET /api/log/summary - Manager session should return log counts summary map")
    void summaryReturnsLogCountsWhenUserIsManager() throws Exception {
        Map<String, Long> mockSummary = Map.of("ERROR", 5L, "INFO", 12L);
        when(systemLogService.summary()).thenReturn(mockSummary);

        ResultActions resposta = mockMvc.perform(get("/api/log/summary").session(managerSession));

        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.ERROR").value(5))
                .andExpect(jsonPath("$.INFO").value(12));
        verify(systemLogService, times(1)).summary();
    }

    @Test
    @DisplayName("DELETE /api/log/deleteAll - Manager session should delete all logs")
    void deleteAllReturns200WhenUserIsManager() throws Exception {
        when(systemLogService.deleteAll()).thenReturn("All logs deleted successfully");

        ResultActions resposta = mockMvc.perform(delete("/api/log/deleteAll").session(managerSession));

        resposta.andExpect(status().isOk())
                .andExpect(content().string("All logs deleted successfully"));
        verify(systemLogService, times(1)).deleteAll();
    }
}