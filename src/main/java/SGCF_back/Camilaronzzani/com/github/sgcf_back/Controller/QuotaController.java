package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.QuotaDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.QuotaRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.QuotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/Quota")
@CrossOrigin(origins = "http://localhost:4200")
public class QuotaController {
    @Autowired
    private QuotaService quotaService;

    @GetMapping("/findAll")
    public ResponseEntity<List<QuotaDto>> findAll() {
        try {
            return ResponseEntity.ok(quotaService.findAll());
        } catch (ResponseStatusException err) {
            return ResponseEntity.status(err.getStatusCode()).build();
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findId/{id}")
    public ResponseEntity<QuotaDto> findById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(quotaService.findById(id));
        } catch (ResponseStatusException err) {
            return ResponseEntity.status(err.getStatusCode()).build();
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody QuotaRequest quotaRequest) {
        try {
            return ResponseEntity.ok(quotaService.save(quotaRequest));
        } catch (ResponseStatusException err) {
            return ResponseEntity.status(err.getStatusCode()).body(err.getReason());
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<String> update(@RequestBody QuotaRequest quotaRequest, @PathVariable long id) {
        try {
            return ResponseEntity.ok(quotaService.update(quotaRequest, id));
        } catch (ResponseStatusException err) {
            return ResponseEntity.status(err.getStatusCode()).body(err.getReason());
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        try {
            return ResponseEntity.ok(quotaService.delete(id));
        } catch (ResponseStatusException err) {
            return ResponseEntity.status(err.getStatusCode()).body(err.getReason());
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/updatePatch/{id}")
    public ResponseEntity<String> updatePartial(@PathVariable long id, @RequestBody Map<String, Object> quota) {
        try {
            String message = quotaService.applyPartialUpdate(id, quota);
            return ResponseEntity.ok(message);
        } catch (ResponseStatusException err) {
            return ResponseEntity.status(err.getStatusCode()).body(err.getReason());
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findAll/active")
    public ResponseEntity<List<QuotaDto>> findAllActive() {
        try {
            return ResponseEntity.ok(quotaService.findAllActive());
        } catch (ResponseStatusException err) {
            return ResponseEntity.status(err.getStatusCode()).build();
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findByEmployee/{employeeId}")
    public ResponseEntity<List<QuotaDto>> findByEmployee(@PathVariable long employeeId) {
        try {
            return ResponseEntity.ok(quotaService.findByEmployee(employeeId));
        } catch (ResponseStatusException err) {
            return ResponseEntity.status(err.getStatusCode()).build();
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findAll/company")
    public ResponseEntity<List<QuotaDto>> findAllCompany() {
        try {
            return ResponseEntity.ok(quotaService.findAllCompany());
        } catch (ResponseStatusException err) {
            return ResponseEntity.status(err.getStatusCode()).build();
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findAll/progress")
    public ResponseEntity<List<QuotaDto>> findAllProgress() {
        try {
            return ResponseEntity.ok(quotaService.findAllProgress());
        } catch (ResponseStatusException err) {
            return ResponseEntity.status(err.getStatusCode()).build();
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }
}
