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
    public ResponseEntity save(@Valid @RequestBody QuotaRequest quotaRequest) {
            quotaService.save(quotaRequest);
            return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@Valid @RequestBody QuotaRequest quotaRequest, @PathVariable long id) {
            QuotaDto quotaDto = quotaService.toDto(quotaService.update(quotaRequest, id));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable long id) {
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
