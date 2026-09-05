package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.QuotaDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.QuotaRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Quota;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.QuotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/quota")
public class QuotaController {
    @Autowired
    private QuotaService quotaService;

    @GetMapping("/findAll")
    public ResponseEntity<List<QuotaDto>> findAll() {
        try {
            List<Quota> quotaList = quotaService.findAll();
            List<QuotaDto> quotaDtos = new ArrayList<>();
            quotaList.forEach(quota -> {
                QuotaDto quotaDto = quotaService.toDto(quota);
                quotaDtos.add(quotaDto);
            });
            return ResponseEntity.ok(quotaDtos);

        } catch (ResponseStatusException err) {
            return ResponseEntity.status(err.getStatusCode()).build();
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findId/{id}")
    public ResponseEntity<QuotaDto> findById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(quotaService.toDto(quotaService.findById(id)));
        } catch (ResponseStatusException err) {
            return ResponseEntity.status(err.getStatusCode()).build();
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody QuotaRequest quotaRequest) {
        try {
            return new ResponseEntity<>(quotaService.save(quotaRequest), HttpStatus.NO_CONTENT);
        } catch (ResponseStatusException err) {
            return ResponseEntity.status(err.getStatusCode()).body(err.getReason());
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<String> update(@RequestBody QuotaRequest quotaRequest, @PathVariable long id) {
        try {
            return new ResponseEntity<>(quotaService.update(quotaRequest, id),HttpStatus.NO_CONTENT);
        } catch (ResponseStatusException err) {
            return ResponseEntity.status(err.getStatusCode()).body(err.getReason());
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        try {
            return new ResponseEntity<>(quotaService.delete(id), HttpStatus.NO_CONTENT);
        } catch (ResponseStatusException err) {
            return ResponseEntity.status(err.getStatusCode()).body(err.getReason());
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<String> updatePartial(@PathVariable long id, @RequestBody Map<String, Object> quota) {
        try {
            String message = quotaService.applyPartialUpdate(id, quota);
            return new ResponseEntity<>(message, HttpStatus.NO_CONTENT);
        } catch (ResponseStatusException err) {
            return ResponseEntity.status(err.getStatusCode()).body(err.getReason());
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findAll/active")
    public ResponseEntity<List<QuotaDto>> findAllActive() {
        try {

            List<Quota> quotaList = quotaService.findAllActive();
            List<QuotaDto> quotaDtos = new ArrayList<>();
            quotaList.forEach(quota -> {
                QuotaDto quotaDto = quotaService.toDto(quota);
                quotaDtos.add(quotaDto);
            });
            return ResponseEntity.ok(quotaDtos);
        } catch (ResponseStatusException err) {
            return ResponseEntity.status(err.getStatusCode()).build();
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findByEmployee/{employeeId}")
    public ResponseEntity<List<QuotaDto>> findByEmployee(@PathVariable long employeeId) {
        try {
            List<Quota> quotaList =quotaService.findByEmployee(employeeId);
            List<QuotaDto> quotaDtos = new ArrayList<>();
            quotaList.forEach(quota -> {
                QuotaDto quotaDto = quotaService.toDto(quota);
                quotaDtos.add(quotaDto);
            });
            return ResponseEntity.ok(quotaDtos);
        } catch (ResponseStatusException err) {
            return ResponseEntity.status(err.getStatusCode()).build();
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findAll/company")
    public ResponseEntity<List<QuotaDto>> findAllCompany() {
        try {
            List<Quota> quotaList =quotaService.findAllCompany();
            List<QuotaDto> quotaDtos = new ArrayList<>();
            quotaList.forEach(quota -> {
                QuotaDto quotaDto = quotaService.toDto(quota);
                quotaDtos.add(quotaDto);
            });

            return ResponseEntity.ok(quotaDtos);
        } catch (ResponseStatusException err) {
            return ResponseEntity.status(err.getStatusCode()).build();
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }


}
