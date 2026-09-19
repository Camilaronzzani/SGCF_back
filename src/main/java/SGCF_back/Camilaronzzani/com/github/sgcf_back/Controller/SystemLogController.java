package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.SystemLogDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Permission;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.SystemLogService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/log")
public class SystemLogController {

    @Autowired
    private SystemLogService systemLogService;

    @GetMapping("/findAll")
    public ResponseEntity<List<SystemLogDto>> findAll(@RequestParam(required = false) String level,
                                                      HttpSession session) {
            requireManager(session);
            List<SystemLogDto> systemLogDtos = systemLogService.findAll(level)
                    .stream()
                    .map(SystemLogDto :: toDto)
                    .toList();
            return ResponseEntity.ok(systemLogDtos);
    }

    @GetMapping("/findId/{id}")
    public ResponseEntity<SystemLogDto> findById(@PathVariable long id, HttpSession session) {
            requireManager(session);
            return ResponseEntity.ok(SystemLogDto.toDto(systemLogService.findById(id)));
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Long>> summary(HttpSession session) {
            requireManager(session);
            return ResponseEntity.ok(systemLogService.summary());
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAll(HttpSession session) {
            requireManager(session);
            return ResponseEntity.ok(systemLogService.deleteAll());
    }

    private void requireManager(HttpSession session) {
        if (session.getAttribute("userId") == null || session.getAttribute("permission") != Permission.Manager) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}
