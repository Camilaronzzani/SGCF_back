package sgcf_back.camilaronzzani.com.github.sgcf_back.controller;

import org.springframework.security.access.prepost.PreAuthorize;

import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.SystemLogDto;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.SystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/log")
public class SystemLogController {

    @Autowired
    private SystemLogService systemLogService;

    @PreAuthorize("hasAuthority('MANAGER')")
    @GetMapping("/findAll")
    public ResponseEntity<List<SystemLogDto>> findAll(@RequestParam(required = false) String level) {
            List<SystemLogDto> systemLogDtos = systemLogService.findAll(level)
                    .stream()
                    .map(SystemLogDto :: toDto)
                    .toList();
            return ResponseEntity.ok(systemLogDtos);
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @GetMapping("/findId/{id}")
    public ResponseEntity<SystemLogDto> findById(@PathVariable long id) {
            return ResponseEntity.ok(SystemLogDto.toDto(systemLogService.findById(id)));
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Long>> summary() {
            return ResponseEntity.ok(systemLogService.summary());
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAll() {
            return ResponseEntity.ok(systemLogService.deleteAll());
    }
}
