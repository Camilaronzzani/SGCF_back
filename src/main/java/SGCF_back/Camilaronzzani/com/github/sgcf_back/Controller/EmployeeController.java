package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller;


import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.EmployeDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.AuthenticateRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.DeactivationRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.EmployeeRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.EmployeeService;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpSession;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Permission;

@RestController
@RequestMapping("api/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private UserService userService;

    @GetMapping("/findAll")
    public ResponseEntity<List<EmployeDto>> findAll(){
        try {
            List<EmployeDto> employeDtos = employeeService.findAll()
                    .stream()
                    .map(EmployeDto :: toDto)
                    .toList();
            return ResponseEntity.ok(employeDtos);
        } catch (Exception e) {
            return  ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/deactivate/{id}")
    public ResponseEntity deactivate(@PathVariable long id,@Valid @RequestBody DeactivationRequest request) {
        try {
            if (!userService.authenticate(new AuthenticateRequest(request.email(), request.password()))) {
                return ResponseEntity.status(401).build();
            }
            employeeService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/findId/{id}")
    public ResponseEntity<EmployeDto> findById(@PathVariable long id){
        try {
            return ResponseEntity.ok(EmployeDto.toDto(employeeService.findById(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity salve(@Valid @RequestBody EmployeeRequest employeeRequest){
        try {
            employeeService.save(employeeRequest);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<EmployeDto> update(@Valid @RequestBody EmployeeRequest employeeRequest, @PathVariable long id){
        try {
            EmployeDto employeDto = EmployeDto.toDto(employeeService.update(employeeRequest , id));
            return ResponseEntity.ok(employeDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findAll/active")
    public ResponseEntity<List<EmployeDto>> findAllActive (){
        try {
            List<EmployeDto> employeDtos = employeeService.findAllActive()
                    .stream()
                    .map(EmployeDto :: toDto)
                    .toList();
            return ResponseEntity.ok(employeDtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
