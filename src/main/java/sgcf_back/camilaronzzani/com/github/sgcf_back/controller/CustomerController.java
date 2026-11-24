package sgcf_back.camilaronzzani.com.github.sgcf_back.controller;

import org.springframework.security.access.prepost.PreAuthorize;


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

    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")
    @GetMapping("/findAll")
    public ResponseEntity<List<CustomerDto>> findAll(){
            List<CustomerDto> customerDtoList = customerService.findAll()
                    .stream()
                    .map(CustomerDto :: toDto)
                    .toList();
            return ResponseEntity.ok(customerDtoList);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")
    @GetMapping("/findById/{id}")
    public ResponseEntity<CustomerDto> findById(@PathVariable long id){
            return ResponseEntity.ok(CustomerDto.toDto(customerService.findById(id)));
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")
    @PostMapping("/save")
    public ResponseEntity<Void>  salve(@Valid @RequestBody CustomerRequest customerRequest){
            customerService.save(customerRequest);
            return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")
    @PutMapping("/update/{id}")
    public ResponseEntity<CustomerDto> update(@Valid @RequestBody CustomerRequest customerRequest , @PathVariable long id){
            CustomerDto customerDto = CustomerDto.toDto(customerService.update(customerRequest , id));
            return ResponseEntity.ok(customerDto);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id){
            customerService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")
    @GetMapping("/findAll/active")
    public ResponseEntity<List<CustomerDto>> findAllActive (){
            List<CustomerDto> customerDtoList = customerService.findAllActive()
                    .stream()
                    .map(CustomerDto :: toDto)
                    .toList();
            return ResponseEntity.ok(customerDtoList);
    }
}
