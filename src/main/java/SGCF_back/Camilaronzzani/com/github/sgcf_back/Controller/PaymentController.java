package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.PaymentDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.PaymentRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/Payment")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @GetMapping("/findAll")
    public ResponseEntity<List<PaymentDto>> findAll() {
        try {
            return ResponseEntity.ok(paymentService.findAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findId/{id}")
    public ResponseEntity<PaymentDto> findById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(paymentService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody PaymentRequest paymentRequest) {
        try {
            return ResponseEntity.ok(paymentService.save(paymentRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<String> update(@RequestBody PaymentRequest paymentRequest, @PathVariable long id) {
        try {
            return ResponseEntity.ok(paymentService.update(paymentRequest, id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        try {
            return ResponseEntity.ok(paymentService.delete(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/updatePatch/{id}")
    public ResponseEntity<String> updatePartial(@PathVariable long id, @RequestBody Map<String, Object> payment) {
        try {
            String message = paymentService.applyPartialUpdate(id, payment);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findAll/active")
    public ResponseEntity<List<PaymentDto>> findAllActive() {
        try {
            return ResponseEntity.ok(paymentService.findAllActive());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findByCustomer/{customerId}")
    public ResponseEntity<List<PaymentDto>> findByCustomer(@PathVariable long customerId) {
        try {
            return ResponseEntity.ok(paymentService.findByCustomer(customerId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findByStatus/{status}")
    public ResponseEntity<List<PaymentDto>> findByStatus(@PathVariable Status status) {
        try {
            return ResponseEntity.ok(paymentService.findByStatus(status));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
