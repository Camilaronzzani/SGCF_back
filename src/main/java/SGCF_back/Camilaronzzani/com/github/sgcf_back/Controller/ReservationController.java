package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.ReservationRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.ReservationDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/Reservation")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @GetMapping("/findAll")
    public ResponseEntity<List<ReservationDto>> findAll() {
        try {
            return ResponseEntity.ok(reservationService.findAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findId/{id}")
    public ResponseEntity<ReservationDto> findById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(reservationService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody ReservationRequest reservationRequest) {
        try {
            return ResponseEntity.ok(reservationService.save(reservationRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<String> update(@RequestBody ReservationRequest reservationRequest, @PathVariable long id) {
        try {
            return ResponseEntity.ok(reservationService.update(reservationRequest, id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        try {
            return ResponseEntity.ok(reservationService.delete(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/updatePatch/{id}")
    public ResponseEntity<String> updatePartial(@PathVariable long id, @RequestBody Map<String, Object> reservation) {
        try {
            String message = reservationService.applyPartialUpdate(id, reservation);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findAll/active")
    public ResponseEntity<List<ReservationDto>> findAllActive() {
        try {
            return ResponseEntity.ok(reservationService.findAllActive());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findByCustomer/{customerId}")
    public ResponseEntity<List<ReservationDto>> findByCustomer(@PathVariable long customerId) {
        try {
            return ResponseEntity.ok(reservationService.findByCustomer(customerId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findByStatus/{status}")
    public ResponseEntity<List<ReservationDto>> findByStatus(@PathVariable Status status) {
        try {
            return ResponseEntity.ok(reservationService.findByStatus(status));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
