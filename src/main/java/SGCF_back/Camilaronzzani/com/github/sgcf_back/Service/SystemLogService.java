package SGCF_back.Camilaronzzani.com.github.sgcf_back.Service;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.SystemLog;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.SystemLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SystemLogService {

    @Autowired
    private SystemLogRepository systemLogRepository;

    public List<SystemLog> findAll(String level) {
        log.info("Fetches system log list");

        if (level == null || level.isBlank()) {
            return systemLogRepository.findAllByOrderByCreatedAtDesc();
        }

        return systemLogRepository.findByLevelOrderByCreatedAtDesc(level.toUpperCase());
    }

    public SystemLog findById(long id) {
        return systemLogRepository.findById(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "log not found"));
    }

    public Map<String, Long> summary() {
        Map<String, Long> resumo = new HashMap<>();
        resumo.put("total", systemLogRepository.count());
        resumo.put("error", systemLogRepository.countByLevel("ERROR"));
        resumo.put("warn", systemLogRepository.countByLevel("WARN"));

        return resumo;
    }

    public String deleteAll() {
        long total = systemLogRepository.count();
        systemLogRepository.deleteAll();

        log.info("System log cleaned");
        return total + " log(s) removido(s)";
    }
}
