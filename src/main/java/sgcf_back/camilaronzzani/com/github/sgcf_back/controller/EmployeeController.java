package sgcf_back.camilaronzzani.com.github.sgcf_back.controller;

import org.springframework.security.access.prepost.PreAuthorize;


import org.springframework.security.crypto.password.PasswordEncoder;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.EmployeDto;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.AuthenticateRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.DeactivationRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.EmployeeRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.EmployeeService;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")
    @GetMapping("/findAll")
    public ResponseEntity<List<EmployeDto>> findAll(){
            List<EmployeDto> employeDtos = employeeService.findAll()
                    .stream()
                    .map(employeeService :: toSummaryDto)
                    .toList();
            return ResponseEntity.ok(employeDtos);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")
    @DeleteMapping("/deactivate/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable long id,@Valid @RequestBody DeactivationRequest request) {

            if (!passwordEncoder.matches(request.email(), request.password())) {
                return ResponseEntity.status(401).build();
            }
            employeeService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")
    @GetMapping("/findId/{id}")
    public ResponseEntity<EmployeDto> findById(@PathVariable long id){
            return ResponseEntity.ok(EmployeDto.toDto(employeeService.findById(id)));
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")
    @PostMapping("/save")
    public ResponseEntity<Void> salve(@Valid @RequestBody EmployeeRequest employeeRequest){
            employeeService.save(employeeRequest);
            return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")
    @PutMapping("/update/{id}")
    public ResponseEntity<EmployeDto> update(@Valid @RequestBody EmployeeRequest employeeRequest, @PathVariable long id){
            EmployeDto employeDto = EmployeDto.toDto(employeeService.update(employeeRequest , id));
            return ResponseEntity.ok(employeDto);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")
    @GetMapping("/findAll/active")
    public ResponseEntity<List<EmployeDto>> findAllActive (){
            List<EmployeDto> employeDtos = employeeService.findAllActive()
                    .stream()
                    .map(employeeService :: toSummaryDto)
                    .toList();
            return ResponseEntity.ok(employeDtos);
    }
}
