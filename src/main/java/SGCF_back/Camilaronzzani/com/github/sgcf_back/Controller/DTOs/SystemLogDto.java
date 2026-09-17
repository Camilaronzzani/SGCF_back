package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.SystemLog;

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
