package sgcf_back.camilaronzzani.com.github.sgcf_back.ServiceTest;

import org.junit.jupiter.api.BeforeEach;
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
        // Given
        List<SystemLog> expectedLogs = List.of(
                createTestLog(1L, "ERROR", "Error message"),
                createTestLog(2L, "WARN", "Warning message"),
                createTestLog(3L, "INFO", "Info message")
        );
        when(systemLogRepository.findAllByOrderByCreatedAtDesc()).thenReturn(expectedLogs);

        // When
        List<SystemLog> result = systemLogService.findAll(null);

        // Then
        assertSame(expectedLogs, result);
        verify(systemLogRepository).findAllByOrderByCreatedAtDesc();
        verify(systemLogRepository, never()).findByLevelOrderByCreatedAtDesc(anyString());
    }

    @Test
    void findAllWithEmptyLevelReturnsAllLogs() {
        // Given
        List<SystemLog> expectedLogs = List.of(
                createTestLog(1L, "ERROR", "Error message")
        );
        when(systemLogRepository.findAllByOrderByCreatedAtDesc()).thenReturn(expectedLogs);

        // When
        List<SystemLog> result = systemLogService.findAll("");

        // Then
        assertSame(expectedLogs, result);
        verify(systemLogRepository).findAllByOrderByCreatedAtDesc();
        verify(systemLogRepository, never()).findByLevelOrderByCreatedAtDesc(anyString());
    }

    @Test
    void findAllWithBlankLevelReturnsAllLogs() {
        // Given
        List<SystemLog> expectedLogs = List.of(
                createTestLog(1L, "WARN", "Warning message")
        );
        when(systemLogRepository.findAllByOrderByCreatedAtDesc()).thenReturn(expectedLogs);

        // When
        List<SystemLog> result = systemLogService.findAll("   ");

        // Then
        assertSame(expectedLogs, result);
        verify(systemLogRepository).findAllByOrderByCreatedAtDesc();
        verify(systemLogRepository, never()).findByLevelOrderByCreatedAtDesc(anyString());
    }

    @Test
    void findAllWithSpecificLevelReturnsFilteredLogs() {
        // Given
        List<SystemLog> errorLogs = List.of(
                createTestLog(1L, "ERROR", "Error 1"),
                createTestLog(2L, "ERROR", "Error 2")
        );
        when(systemLogRepository.findByLevelOrderByCreatedAtDesc("ERROR")).thenReturn(errorLogs);

        // When
        List<SystemLog> result = systemLogService.findAll("error");

        // Then
        assertSame(errorLogs, result);
        verify(systemLogRepository).findByLevelOrderByCreatedAtDesc("ERROR");
        verify(systemLogRepository, never()).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void findAllWithLevelConvertsToUpperCase() {
        // Given
        List<SystemLog> warnLogs = List.of(createTestLog(1L, "WARN", "Warning"));
        when(systemLogRepository.findByLevelOrderByCreatedAtDesc("WARN")).thenReturn(warnLogs);

        // When
        systemLogService.findAll("warn");

        // Then
        verify(systemLogRepository).findByLevelOrderByCreatedAtDesc("WARN");
    }

    @Test
    void findAllWithLevelReturnsEmptyListWhenNoLogsMatch() {
        // Given
        when(systemLogRepository.findByLevelOrderByCreatedAtDesc("DEBUG")).thenReturn(new ArrayList<>());

        // When
        List<SystemLog> result = systemLogService.findAll("debug");

        // Then
        assertTrue(result.isEmpty());
        verify(systemLogRepository).findByLevelOrderByCreatedAtDesc("DEBUG");
    }

    @Test
    void findByIdReturnsLogWhenExists() {
        // Given
        SystemLog expectedLog = createTestLog(1L, "ERROR", "Test error");
        when(systemLogRepository.findById(1L)).thenReturn(Optional.of(expectedLog));

        // When
        SystemLog result = systemLogService.findById(1L);

        // Then
        assertSame(expectedLog, result);
        assertEquals(1L, result.getId());
        assertEquals("ERROR", result.getLevel());
        assertEquals("Test error", result.getMessage());
        verify(systemLogRepository).findById(1L);
    }

    @Test
    void findByIdThrowsNotFoundWhenLogDoesNotExist() {
        // Given
        when(systemLogRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> systemLogService.findById(999L));

        // Then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("log not found"));
        verify(systemLogRepository).findById(999L);
    }

    @Test
    void findByIdWithZeroIdThrowsNotFound() {
        // Given
        when(systemLogRepository.findById(0L)).thenReturn(Optional.empty());

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> systemLogService.findById(0L));

        // Then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void findByIdWithNegativeIdThrowsNotFound() {
        // Given
        when(systemLogRepository.findById(-1L)).thenReturn(Optional.empty());

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> systemLogService.findById(-1L));

        // Then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void summaryReturnsCorrectCountsWhenLogsExist() {
        // Given
        when(systemLogRepository.count()).thenReturn(100L);
        when(systemLogRepository.countByLevel("ERROR")).thenReturn(15L);
        when(systemLogRepository.countByLevel("WARN")).thenReturn(25L);

        // When
        Map<String, Long> result = systemLogService.summary();

        // Then
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
        // Given
        when(systemLogRepository.count()).thenReturn(0L);
        when(systemLogRepository.countByLevel("ERROR")).thenReturn(0L);
        when(systemLogRepository.countByLevel("WARN")).thenReturn(0L);

        // When
        Map<String, Long> result = systemLogService.summary();

        // Then
        assertNotNull(result);
        assertEquals(0L, result.get("total"));
        assertEquals(0L, result.get("error"));
        assertEquals(0L, result.get("warn"));
    }

    @Test
    void summaryReturnsCorrectCountsWhenOnlyErrorsExist() {
        // Given
        when(systemLogRepository.count()).thenReturn(10L);
        when(systemLogRepository.countByLevel("ERROR")).thenReturn(10L);
        when(systemLogRepository.countByLevel("WARN")).thenReturn(0L);

        // When
        Map<String, Long> result = systemLogService.summary();

        // Then
        assertEquals(10L, result.get("total"));
        assertEquals(10L, result.get("error"));
        assertEquals(0L, result.get("warn"));
    }

    @Test
    void summaryReturnsCorrectCountsWhenOnlyWarningsExist() {
        // Given
        when(systemLogRepository.count()).thenReturn(5L);
        when(systemLogRepository.countByLevel("ERROR")).thenReturn(0L);
        when(systemLogRepository.countByLevel("WARN")).thenReturn(5L);

        // When
        Map<String, Long> result = systemLogService.summary();

        // Then
        assertEquals(5L, result.get("total"));
        assertEquals(0L, result.get("error"));
        assertEquals(5L, result.get("warn"));
    }

    @Test
    void deleteAllRemovesAllLogsAndReturnsCorrectMessage() {
        // Given
        when(systemLogRepository.count()).thenReturn(50L);

        // When
        String result = systemLogService.deleteAll();

        // Then
        assertEquals("50 log(s) removido(s)", result);
        verify(systemLogRepository).count();
        verify(systemLogRepository).deleteAll();
    }

    @Test
    void deleteAllWorksWhenNoLogsExist() {
        // Given
        when(systemLogRepository.count()).thenReturn(0L);

        // When
        String result = systemLogService.deleteAll();

        // Then
        assertEquals("0 log(s) removido(s)", result);
        verify(systemLogRepository).count();
        verify(systemLogRepository).deleteAll();
    }

    @Test
    void deleteAllRemovesSingleLog() {
        // Given
        when(systemLogRepository.count()).thenReturn(1L);

        // When
        String result = systemLogService.deleteAll();

        // Then
        assertEquals("1 log(s) removido(s)", result);
        verify(systemLogRepository).deleteAll();
    }

    @Test
    void deleteAllRemovesLargeNumberOfLogs() {
        // Given
        when(systemLogRepository.count()).thenReturn(10000L);

        // When
        String result = systemLogService.deleteAll();

        // Then
        assertEquals("10000 log(s) removido(s)", result);
        verify(systemLogRepository).count();
        verify(systemLogRepository).deleteAll();
    }
}
