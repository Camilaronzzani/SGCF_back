package Controller;


import Controller.DTOs.CustomerDto;
import Controller.DTOs.Request.CustomerRequest;
import Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/Customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/findAll")
    public ResponseEntity<List<CustomerDto>> findAll(){
        try {
            return ResponseEntity.ok(customerService.findAll());
        } catch (Exception e) {
            return (ResponseEntity<List<CustomerDto>>) ResponseEntity.badRequest();
        }
    }

    @GetMapping("/findId/{id}")
    public ResponseEntity<CustomerDto> findById(@PathVariable long id){
        try {
            return ResponseEntity.ok(customerService.findById(id));
        } catch (Exception e) {
            return (ResponseEntity<CustomerDto>) ResponseEntity.badRequest();
        }
    }

    @PostMapping("/save")
    public ResponseEntity<String> salve(@RequestBody CustomerRequest customerRequest){
        try {
            return ResponseEntity.ok(customerService.save(customerRequest));
        } catch (Exception e) {
            return (ResponseEntity<String>) ResponseEntity.badRequest();
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<String> update(@RequestBody CustomerRequest customerRequest , @PathVariable long id){
        try {
            return ResponseEntity.ok(customerService.update(customerRequest , id));
        } catch (Exception e) {
            return (ResponseEntity<String>) ResponseEntity.badRequest();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable long id){
        try {
            return ResponseEntity.ok(customerService.delete(id));
        } catch (Exception e) {
            return (ResponseEntity<String>) ResponseEntity.badRequest();
        }
    }

    @PatchMapping("/updatePatch/{id}")
    public ResponseEntity<String> updatePartial(@PathVariable long id , @RequestBody Map<String , Object> customer){
        try {

            String message = customerService.applyPartialUpdate(id , customer);
            return ResponseEntity.ok(message);

        } catch (Exception e) {
            return (ResponseEntity<String>) ResponseEntity.badRequest();
        }
    }

    @GetMapping("/findAll/active")
    public ResponseEntity<List<CustomerDto>> findAllActive (){
        try {
            return ResponseEntity.ok(customerService.findAllActive());
        } catch (Exception e) {
            return (ResponseEntity<List<CustomerDto>>) ResponseEntity.badRequest();
        }
    }
}
