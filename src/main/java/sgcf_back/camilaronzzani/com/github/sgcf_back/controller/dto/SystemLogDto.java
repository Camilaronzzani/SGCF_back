package sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto;

import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.SystemLog;

import java.time.LocalDateTime;

public record SystemLogDto(Long id, String level, String loggerName, String message,
                           String exceptionType, String stackTrace, LocalDateTime createdAt) {
    public static SystemLogDto toDto(SystemLog systemLog) {
        return new SystemLogDto(
                systemLog.getId(),
                systemLog.getLevel(),
                systemLog.getLoggerName(),
                systemLog.getMessage(),
                systemLog.getExceptionType(),
                systemLog.getStackTrace(),
                systemLog.getCreatedAt()
        );
    }
}
