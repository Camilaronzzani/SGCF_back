package SGCF_back.Camilaronzzani.com.github.sgcf_back.Config;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.SystemLog;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.SystemLogRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SystemLogStore {

    private static SystemLogRepository repository;

    private static boolean salvando = false;

    @Autowired
    private SystemLogRepository systemLogRepository;

    @PostConstruct
    public void init() {
        repository = systemLogRepository;
    }

    public static void record(String level, String loggerName, String message,
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
            System.out.println("Nao foi possivel gravar o log no banco");
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
