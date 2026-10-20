package sgcf_back.camilaronzzani.com.github.sgcf_back.controller;

import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.PaymentDto;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.PaymentRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Status;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.CurrencyService;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/Payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CurrencyService currencyService;

    @GetMapping("/findAll")
    public ResponseEntity<List<PaymentDto>> findAll() {
            List<PaymentDto> paymentDtos = paymentService.findAll().
                    stream()
                    .map(PaymentDto :: toDto)
                    .toList();
            return ResponseEntity.ok(paymentDtos);
    }

    @GetMapping("/findId/{id}")
    public ResponseEntity<PaymentDto> findById(@PathVariable long id) {
            return ResponseEntity.ok(PaymentDto.toDto(paymentService.findById(id)));
    }

    @PostMapping("/save")
    public ResponseEntity save(@Valid @RequestBody PaymentRequest paymentRequest) {
            paymentService.save(paymentRequest);
            return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PaymentDto> update(@Valid @RequestBody PaymentRequest paymentRequest, @PathVariable long id) {
            PaymentDto paymentDto = PaymentDto.toDto(paymentService.update(paymentRequest, id));
            return ResponseEntity.ok(paymentDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable long id) {
            paymentService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping("/findAll/active")
    public ResponseEntity<List<PaymentDto>> findAllActive() {
            List<PaymentDto> paymentDtos = paymentService.findAllActive()
                    .stream()
                    .map(PaymentDto :: toDto)
                    .toList();
            return ResponseEntity.ok(paymentDtos);
    }

    @GetMapping("/findByCustomer/{customerId}")
    public ResponseEntity<List<PaymentDto>> findByCustomer(@PathVariable long customerId) {
            List<PaymentDto> paymentDtos = paymentService.findByCustomer(customerId)
                    .stream()
                    .map(PaymentDto :: toDto)
                    .toList();
            return ResponseEntity.ok(paymentDtos);
    }

    @GetMapping("/findByStatus/{status}")
    public ResponseEntity<List<PaymentDto>> findByStatus(@PathVariable Status status) {
            List<PaymentDto> paymentDtos = paymentService.findByStatus(status)
                    .stream()
                    .map(PaymentDto :: toDto)
                    .toList();
            return ResponseEntity.ok(paymentDtos);
    }

    @GetMapping("/quotations")
    public ResponseEntity<Double> getQuotations(){
        return new ResponseEntity<>(currencyService.getQuotation("USD"), HttpStatus.OK);
    }

    }