package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller;


import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.CustomerDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.CustomerRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/findAll")
    public ResponseEntity<List<CustomerDto>> findAll(){
        try {
            List<CustomerDto> customerDtoList = customerService.findAll()
                    .stream()
                    .map(CustomerDto :: toDto)
                    .toList();
            return ResponseEntity.ok(customerDtoList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<CustomerDto> findById(@PathVariable long id){
        try {
            return ResponseEntity.ok(CustomerDto.toDto(customerService.findById(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity<String> salve(@RequestBody CustomerRequest customerRequest){
        try {
            return new ResponseEntity<>(customerService.save(customerRequest), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<String> update(@RequestBody CustomerRequest customerRequest , @PathVariable long id){
        try {
            return new ResponseEntity<>(customerService.update(customerRequest , id),HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable long id){
        try {
            return new ResponseEntity<>(customerService.delete(id),HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<String> updatePartial(@PathVariable long id , @RequestBody Map<String , Object> customer){
        try {

            String message = customerService.applyPartialUpdate(id , customer);
            return new ResponseEntity<>(message , HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findAll/active")
    public ResponseEntity<List<CustomerDto>> findAllActive (){
        try {
            List<CustomerDto> customerDtoList = customerService.findAllActive()
                    .stream()
                    .map(CustomerDto :: toDto)
                    .toList();
            return ResponseEntity.ok(customerDtoList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
