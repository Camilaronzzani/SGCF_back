package SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_log")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SystemLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "level", nullable = false, length = 10)
    private String level;

    @Column(name = "logger_name", length = 255)
    private String loggerName;

    @Column(name = "message", length = 2000)
    private String message;

    @Column(name = "exception_type", length = 255)
    private String exceptionType;

    @Column(name = "stack_trace", length = 8000)
    private String stackTrace;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
