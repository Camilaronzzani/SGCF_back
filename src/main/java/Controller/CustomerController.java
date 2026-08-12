package Controller;


import Controller.DTOs.CustomerDto;
import Controller.DTOs.Request.CustomerRequest;
import Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PostMapping("/salve")
    public ResponseEntity<String> salve(@RequestBody CustomerRequest customerRequest){
        try {
            return ResponseEntity.ok(customerService.salve(customerRequest));
        } catch (Exception e) {
            return (ResponseEntity<String>) ResponseEntity.badRequest();
        }
    }
}
