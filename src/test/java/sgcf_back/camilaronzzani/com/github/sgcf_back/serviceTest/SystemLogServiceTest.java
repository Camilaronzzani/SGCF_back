package sgcf_back.camilaronzzani.com.github.sgcf_back.serviceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.SystemLog;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.SystemLogRepository;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.SystemLogService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemLogServiceTest {

    @Mock
    private SystemLogRepository systemLogRepository;

    @InjectMocks
    private SystemLogService systemLogService;

    private SystemLog createTestLog(Long id, String level, String message) {
        SystemLog log = new SystemLog();
        log.setId(id);
        log.setLevel(level);
        log.setLoggerName("TestLogger");
        log.setMessage(message);
        log.setCreatedAt(LocalDateTime.now());
        return log;
    }

    @Test
    void findAllWithNullLevelReturnsAllLogs() {
        List<SystemLog> expectedLogs = List.of(
                createTestLog(1L, "ERROR", "Error message"),
                createTestLog(2L, "WARN", "Warning message"),
                createTestLog(3L, "INFO", "Info message")
        );
        when(systemLogRepository.findAllByOrderByCreatedAtDesc()).thenReturn(expectedLogs);

        List<SystemLog> result = systemLogService.findAll(null);

        assertSame(expectedLogs, result);
        verify(systemLogRepository).findAllByOrderByCreatedAtDesc();
        verify(systemLogRepository, never()).findByLevelOrderByCreatedAtDesc(anyString());
    }

    @Test
    void findAllWithEmptyLevelReturnsAllLogs() {
        List<SystemLog> expectedLogs = List.of(
                createTestLog(1L, "ERROR", "Error message")
        );
        when(systemLogRepository.findAllByOrderByCreatedAtDesc()).thenReturn(expectedLogs);

        List<SystemLog> result = systemLogService.findAll("");

        assertSame(expectedLogs, result);
        verify(systemLogRepository).findAllByOrderByCreatedAtDesc();
        verify(systemLogRepository, never()).findByLevelOrderByCreatedAtDesc(anyString());
    }

    @Test
    void findAllWithBlankLevelReturnsAllLogs() {
        List<SystemLog> expectedLogs = List.of(
                createTestLog(1L, "WARN", "Warning message")
        );
        when(systemLogRepository.findAllByOrderByCreatedAtDesc()).thenReturn(expectedLogs);

        List<SystemLog> result = systemLogService.findAll("   ");

        assertSame(expectedLogs, result);
        verify(systemLogRepository).findAllByOrderByCreatedAtDesc();
        verify(systemLogRepository, never()).findByLevelOrderByCreatedAtDesc(anyString());
    }

    @Test
    void findAllWithSpecificLevelReturnsFilteredLogs() {
        List<SystemLog> errorLogs = List.of(
                createTestLog(1L, "ERROR", "Error 1"),
                createTestLog(2L, "ERROR", "Error 2")
        );
        when(systemLogRepository.findByLevelOrderByCreatedAtDesc("ERROR")).thenReturn(errorLogs);

        List<SystemLog> result = systemLogService.findAll("error");

        assertSame(errorLogs, result);
        verify(systemLogRepository).findByLevelOrderByCreatedAtDesc("ERROR");
        verify(systemLogRepository, never()).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void findAllWithLevelConvertsToUpperCase() {
        List<SystemLog> warnLogs = List.of(createTestLog(1L, "WARN", "Warning"));
        when(systemLogRepository.findByLevelOrderByCreatedAtDesc("WARN")).thenReturn(warnLogs);

        systemLogService.findAll("warn");

        verify(systemLogRepository).findByLevelOrderByCreatedAtDesc("WARN");
    }

    @Test
    void findAllWithLevelReturnsEmptyListWhenNoLogsMatch() {
        when(systemLogRepository.findByLevelOrderByCreatedAtDesc("DEBUG")).thenReturn(new ArrayList<>());

        List<SystemLog> result = systemLogService.findAll("debug");

        assertTrue(result.isEmpty());
        verify(systemLogRepository).findByLevelOrderByCreatedAtDesc("DEBUG");
    }

    @Test
    void findByIdReturnsLogWhenExists() {
        SystemLog expectedLog = createTestLog(1L, "ERROR", "Test error");
        when(systemLogRepository.findById(1L)).thenReturn(Optional.of(expectedLog));

        SystemLog result = systemLogService.findById(1L);

        assertSame(expectedLog, result);
        assertEquals(1L, result.getId());
        assertEquals("ERROR", result.getLevel());
        assertEquals("Test error", result.getMessage());
        verify(systemLogRepository).findById(1L);
    }

    @Test
    void findByIdThrowsNotFoundWhenLogDoesNotExist() {
        when(systemLogRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> systemLogService.findById(999L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("log not found"));
        verify(systemLogRepository).findById(999L);
    }

    @Test
    void findByIdWithZeroIdThrowsNotFound() {
        when(systemLogRepository.findById(0L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> systemLogService.findById(0L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void findByIdWithNegativeIdThrowsNotFound() {
        when(systemLogRepository.findById(-1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> systemLogService.findById(-1L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void summaryReturnsCorrectCountsWhenLogsExist() {
        when(systemLogRepository.count()).thenReturn(100L);
        when(systemLogRepository.countByLevel("ERROR")).thenReturn(15L);
        when(systemLogRepository.countByLevel("WARN")).thenReturn(25L);

        Map<String, Long> result = systemLogService.summary();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(100L, result.get("total"));
        assertEquals(15L, result.get("error"));
        assertEquals(25L, result.get("warn"));
        verify(systemLogRepository).count();
        verify(systemLogRepository).countByLevel("ERROR");
        verify(systemLogRepository).countByLevel("WARN");
    }

    @Test
    void summaryReturnsZeroCountsWhenNoLogsExist() {
        when(systemLogRepository.count()).thenReturn(0L);
        when(systemLogRepository.countByLevel("ERROR")).thenReturn(0L);
        when(systemLogRepository.countByLevel("WARN")).thenReturn(0L);

        Map<String, Long> result = systemLogService.summary();

        assertNotNull(result);
        assertEquals(0L, result.get("total"));
        assertEquals(0L, result.get("error"));
        assertEquals(0L, result.get("warn"));
    }

    @Test
    void summaryReturnsCorrectCountsWhenOnlyErrorsExist() {
        when(systemLogRepository.count()).thenReturn(10L);
        when(systemLogRepository.countByLevel("ERROR")).thenReturn(10L);
        when(systemLogRepository.countByLevel("WARN")).thenReturn(0L);

        Map<String, Long> result = systemLogService.summary();

        assertEquals(10L, result.get("total"));
        assertEquals(10L, result.get("error"));
        assertEquals(0L, result.get("warn"));
    }

    @Test
    void summaryReturnsCorrectCountsWhenOnlyWarningsExist() {
        when(systemLogRepository.count()).thenReturn(5L);
        when(systemLogRepository.countByLevel("ERROR")).thenReturn(0L);
        when(systemLogRepository.countByLevel("WARN")).thenReturn(5L);

        Map<String, Long> result = systemLogService.summary();

        assertEquals(5L, result.get("total"));
        assertEquals(0L, result.get("error"));
        assertEquals(5L, result.get("warn"));
    }

    @Test
    void deleteAllRemovesAllLogsAndReturnsCorrectMessage() {
        when(systemLogRepository.count()).thenReturn(50L);

        String result = systemLogService.deleteAll();

        assertEquals("50 log(s) removido(s)", result);
        verify(systemLogRepository).count();
        verify(systemLogRepository).deleteAll();
    }

    @Test
    void deleteAllWorksWhenNoLogsExist() {
        when(systemLogRepository.count()).thenReturn(0L);

        String result = systemLogService.deleteAll();

        assertEquals("0 log(s) removido(s)", result);
        verify(systemLogRepository).count();
        verify(systemLogRepository).deleteAll();
    }

    @Test
    void deleteAllRemovesSingleLog() {
        when(systemLogRepository.count()).thenReturn(1L);

        String result = systemLogService.deleteAll();

        assertEquals("1 log(s) removido(s)", result);
        verify(systemLogRepository).deleteAll();
    }

    @Test
    void deleteAllRemovesLargeNumberOfLogs() {
        when(systemLogRepository.count()).thenReturn(10000L);

        String result = systemLogService.deleteAll();

        assertEquals("10000 log(s) removido(s)", result);
        verify(systemLogRepository).count();
        verify(systemLogRepository).deleteAll();
    }
}
