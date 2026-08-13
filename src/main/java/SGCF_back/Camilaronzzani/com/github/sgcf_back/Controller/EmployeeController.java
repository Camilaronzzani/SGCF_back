package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller;


import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.EmployeDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.EmployeeRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/Employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/findAll")
    public ResponseEntity<List<EmployeDto>> findAll(){
        try {
            return ResponseEntity.ok(employeeService.findAll());
        } catch (Exception e) {
            return (ResponseEntity<List<EmployeDto>>) ResponseEntity.badRequest();
        }
    }

    @GetMapping("/findId/{id}")
    public ResponseEntity<EmployeDto> findById(@PathVariable long id){
        try {
            return ResponseEntity.ok(employeeService.findById(id));
        } catch (Exception e) {
            return (ResponseEntity<EmployeDto>) ResponseEntity.badRequest();
        }
    }

    @PostMapping("/save")
    public ResponseEntity<String> salve(@RequestBody EmployeeRequest employeeRequest){
        try {
            return ResponseEntity.ok(employeeService.save(employeeRequest));
        } catch (Exception e) {
            return (ResponseEntity<String>) ResponseEntity.badRequest();
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<String> update(@RequestBody EmployeeRequest employeeRequest, @PathVariable long id){
        try {
            return ResponseEntity.ok(employeeService.update(employeeRequest , id));
        } catch (Exception e) {
            return (ResponseEntity<String>) ResponseEntity.badRequest();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable long id){
        try {
            return ResponseEntity.ok(employeeService.delete(id));
        } catch (Exception e) {
            return (ResponseEntity<String>) ResponseEntity.badRequest();
        }
    }

    @PatchMapping("/updatePatch/{id}")
    public ResponseEntity<String> updatePartial(@PathVariable long id , @RequestBody Map<String , Object> employee){
        try {

            String message = employeeService.applyPartialUpdate(id , employee);
            return ResponseEntity.ok(message);

        } catch (Exception e) {
            return (ResponseEntity<String>) ResponseEntity.badRequest();
        }
    }

    @GetMapping("/findAll/active")
    public ResponseEntity<List<EmployeDto>> findAllActive (){
        try {
            return ResponseEntity.ok(employeeService.findAllActive());
        } catch (Exception e) {
            return (ResponseEntity<List<EmployeDto>>) ResponseEntity.badRequest();
        }
    }
}
