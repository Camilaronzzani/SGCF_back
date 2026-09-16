package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.PaymentDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.PaymentRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/Payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @GetMapping("/findAll")
    public ResponseEntity<List<PaymentDto>> findAll() {
        try {
            List<PaymentDto> paymentDtos = paymentService.findAll().
                    stream()
                    .map(PaymentDto :: toDto)
                    .toList();
            return ResponseEntity.ok(paymentDtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findId/{id}")
    public ResponseEntity<PaymentDto> findById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(PaymentDto.toDto(paymentService.findById(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity save(@Valid @RequestBody PaymentRequest paymentRequest) {
        try {
            paymentService.save(paymentRequest);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<PaymentDto> update(@Valid @RequestBody PaymentRequest paymentRequest, @PathVariable long id) {
        try {
            PaymentDto paymentDto = PaymentDto.toDto(paymentService.update(paymentRequest, id));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable long id) {
        try {
            paymentService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/findAll/active")
    public ResponseEntity<List<PaymentDto>> findAllActive() {
        try {
            List<PaymentDto> paymentDtos = paymentService.findAllActive()
                    .stream()
                    .map(PaymentDto :: toDto)
                    .toList();
            return ResponseEntity.ok(paymentDtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findByCustomer/{customerId}")
    public ResponseEntity<List<PaymentDto>> findByCustomer(@PathVariable long customerId) {
        try {
            List<PaymentDto> paymentDtos = paymentService.findByCustomer(customerId)
                    .stream()
                    .map(PaymentDto :: toDto)
                    .toList();
            return ResponseEntity.ok(paymentDtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findByStatus/{status}")
    public ResponseEntity<List<PaymentDto>> findByStatus(@PathVariable Status status) {
        try {
            List<PaymentDto> paymentDtos = paymentService.findByStatus(status)
                    .stream()
                    .map(PaymentDto :: toDto)
                    .toList();
            return ResponseEntity.ok(paymentDtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
