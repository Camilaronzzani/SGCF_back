package sgcf_back.camilaronzzani.com.github.sgcf_back.controller;


import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.CustomerDto;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.CustomerRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/findAll")
    public ResponseEntity<List<CustomerDto>> findAll(){
            List<CustomerDto> customerDtoList = customerService.findAll()
                    .stream()
                    .map(CustomerDto :: toDto)
                    .toList();
            return ResponseEntity.ok(customerDtoList);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<CustomerDto> findById(@PathVariable long id){
            return ResponseEntity.ok(CustomerDto.toDto(customerService.findById(id)));
    }

    @PostMapping("/save")
    public ResponseEntity<Void>  salve(@Valid @RequestBody CustomerRequest customerRequest){
            customerService.save(customerRequest);
            return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CustomerDto> update(@Valid @RequestBody CustomerRequest customerRequest , @PathVariable long id){
            CustomerDto customerDto = CustomerDto.toDto(customerService.update(customerRequest , id));
            return ResponseEntity.ok(customerDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable long id){
            customerService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/findAll/active")
    public ResponseEntity<List<CustomerDto>> findAllActive (){
            List<CustomerDto> customerDtoList = customerService.findAllActive()
                    .stream()
                    .map(CustomerDto :: toDto)
                    .toList();
            return ResponseEntity.ok(customerDtoList);
    }
}
