package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.QuotaDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.QuotaRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Quota;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.QuotaService;
import jakarta.validation.Valid;
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
        // mudar aqui: tirar o try/catch, o ApiExceptionHandler ja trata (exemplo em TourController.findById)
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
        // mudar aqui: tirar o try/catch, o ApiExceptionHandler ja trata (exemplo em TourController.findById)
        try {
            return ResponseEntity.ok(quotaService.toDto(quotaService.findById(id)));
        } catch (ResponseStatusException err) {
            return ResponseEntity.status(err.getStatusCode()).build();
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity save(@Valid @RequestBody QuotaRequest quotaRequest) {
        // mudar aqui: tirar o try/catch, o ApiExceptionHandler ja trata (exemplo em TourController.findById)
        try {
            quotaService.save(quotaRequest);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (ResponseStatusException err) {
            return ResponseEntity.status(err.getStatusCode()).body(err.getReason());
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@Valid @RequestBody QuotaRequest quotaRequest, @PathVariable long id) {
        // mudar aqui: tirar o try/catch, o ApiExceptionHandler ja trata (exemplo em TourController.findById)
        try {
            QuotaDto quotaDto = quotaService.toDto(quotaService.update(quotaRequest, id));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ResponseStatusException err) {
            return ResponseEntity.status(err.getStatusCode()).body(err.getReason());
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable long id) {
        // mudar aqui: tirar o try/catch, o ApiExceptionHandler ja trata (exemplo em TourController.findById)
        try {
            quotaService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ResponseStatusException err) {
            return ResponseEntity.status(err.getStatusCode()).body(err.getReason());
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }



    @GetMapping("/findAll/active")
    public ResponseEntity<List<QuotaDto>> findAllActive() {
        // mudar aqui: tirar o try/catch, o ApiExceptionHandler ja trata (exemplo em TourController.findById)
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
        // mudar aqui: tirar o try/catch, o ApiExceptionHandler ja trata (exemplo em TourController.findById)
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
        // mudar aqui: tirar o try/catch, o ApiExceptionHandler ja trata (exemplo em TourController.findById)
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
