package sgcf_back.camilaronzzani.com.github.sgcf_back.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.SystemLog;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.SystemLogRepository;

import java.time.LocalDateTime;

@Component
@Slf4j
public class SystemLogStore {

    private static SystemLogRepository repository;

    private static boolean salvando = false;

    public SystemLogStore(SystemLogRepository systemLogRepository) {
        SystemLogStore.repository = systemLogRepository;
    }

    public static void writeLog(String level, String loggerName, String message,
                                String exceptionType, String stackTrace) {
        if (repository == null || salvando) {
            return;
        }

        salvando = true;

        try {
            SystemLog systemLog = new SystemLog();
            systemLog.setLevel(level);
            systemLog.setLoggerName(cortar(loggerName, 255));
            systemLog.setMessage(cortar(message, 2000));
            systemLog.setExceptionType(cortar(exceptionType, 255));
            systemLog.setStackTrace(cortar(stackTrace, 8000));
            systemLog.setCreatedAt(LocalDateTime.now());

            repository.save(systemLog);
        } catch (Exception e) {
            log.error("Nao foi possivel gravar o log no banco", e);
        } finally {
            salvando = false;
        }
    }

    private static String cortar(String texto, int limite) {
        if (texto == null) {
            return null;
        }

        if (texto.length() <= limite) {
            return texto;
        }

        return texto.substring(0, limite);
    }
}