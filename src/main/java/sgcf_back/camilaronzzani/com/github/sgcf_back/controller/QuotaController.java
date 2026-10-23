package sgcf_back.camilaronzzani.com.github.sgcf_back.controller;

import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.QuotaDto;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.QuotaRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Quota;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.QuotaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/quota")
public class QuotaController {
    @Autowired
    private QuotaService quotaService;

    @GetMapping("/findAll")
    public ResponseEntity<List<QuotaDto>> findAll() {
            List<Quota> quotaList = quotaService.findAll();
            List<QuotaDto> quotaDtos = new ArrayList<>();
            quotaList.forEach(quota -> {
                QuotaDto quotaDto = quotaService.toDto(quota);
                quotaDtos.add(quotaDto);
            });
            return ResponseEntity.ok(quotaDtos);
    }

    @GetMapping("/findId/{id}")
    public ResponseEntity<QuotaDto> findById(@PathVariable long id) {
            return ResponseEntity.ok(quotaService.toDto(quotaService.findById(id)));
    }

    @PostMapping("/save")
    public ResponseEntity<Void> save(@Valid @RequestBody QuotaRequest quotaRequest) {
            quotaService.save(quotaRequest);
            return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Void> update(@Valid @RequestBody QuotaRequest quotaRequest, @PathVariable long id) {
            quotaService.update(quotaRequest, id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
            quotaService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    @GetMapping("/findAll/active")
    public ResponseEntity<List<QuotaDto>> findAllActive() {
            List<Quota> quotaList = quotaService.findAllActive();
            List<QuotaDto> quotaDtos = new ArrayList<>();
            quotaList.forEach(quota -> {
                QuotaDto quotaDto = quotaService.toDto(quota);
                quotaDtos.add(quotaDto);
            });
            return ResponseEntity.ok(quotaDtos);
    }

    @GetMapping("/findByEmployee/{employeeId}")
    public ResponseEntity<List<QuotaDto>> findByEmployee(@PathVariable long employeeId) {
            List<Quota> quotaList =quotaService.findByEmployee(employeeId);
            List<QuotaDto> quotaDtos = new ArrayList<>();
            quotaList.forEach(quota -> {
                QuotaDto quotaDto = quotaService.toDto(quota);
                quotaDtos.add(quotaDto);
            });
            return ResponseEntity.ok(quotaDtos);
    }

    @GetMapping("/findAll/company")
    public ResponseEntity<List<QuotaDto>> findAllCompany() {
            List<Quota> quotaList =quotaService.findAllCompany();
            List<QuotaDto> quotaDtos = new ArrayList<>();
            quotaList.forEach(quota -> {
                QuotaDto quotaDto = quotaService.toDto(quota);
                quotaDtos.add(quotaDto);
            });

            return ResponseEntity.ok(quotaDtos);
    }
}
