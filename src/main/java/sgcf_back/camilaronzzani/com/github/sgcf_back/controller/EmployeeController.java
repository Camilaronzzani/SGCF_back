package sgcf_back.camilaronzzani.com.github.sgcf_back.controller;


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
    private UserService userService;

    @GetMapping("/findAll")
    public ResponseEntity<List<EmployeDto>> findAll(){
            List<EmployeDto> employeDtos = employeeService.findAll()
                    .stream()
                    .map(employeeService :: toSummaryDto)
                    .toList();
            return ResponseEntity.ok(employeDtos);
    }

    @DeleteMapping("/deactivate/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable long id,@Valid @RequestBody DeactivationRequest request) {
            if (!userService.authenticate(new AuthenticateRequest(request.email(), request.password()))) {
                return ResponseEntity.status(401).build();
            }
            employeeService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping("/findId/{id}")
    public ResponseEntity<EmployeDto> findById(@PathVariable long id){
            return ResponseEntity.ok(EmployeDto.toDto(employeeService.findById(id)));
    }

    @PostMapping("/save")
    public ResponseEntity<Void> salve(@Valid @RequestBody EmployeeRequest employeeRequest){
            employeeService.save(employeeRequest);
            return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<EmployeDto> update(@Valid @RequestBody EmployeeRequest employeeRequest, @PathVariable long id){
            EmployeDto employeDto = EmployeDto.toDto(employeeService.update(employeeRequest , id));
            return ResponseEntity.ok(employeDto);
    }

    @GetMapping("/findAll/active")
    public ResponseEntity<List<EmployeDto>> findAllActive (){
            List<EmployeDto> employeDtos = employeeService.findAllActive()
                    .stream()
                    .map(employeeService :: toSummaryDto)
                    .toList();
            return ResponseEntity.ok(employeDtos);
    }
}
