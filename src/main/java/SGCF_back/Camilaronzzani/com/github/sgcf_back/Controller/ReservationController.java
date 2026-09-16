package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.BooleanRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.ReservationRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.ReservationDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/reservation")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @GetMapping("/findAll")
    public ResponseEntity<List<ReservationDto>> findAll() {
        try {
            List<ReservationDto> reservationDtos = reservationService.findAll()
                    .stream()
                    .map(ReservationDto :: toDto)
                    .toList();
            return ResponseEntity.ok(reservationDtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findId/{id}")
    public ResponseEntity<ReservationDto> findById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(ReservationDto.toDto(reservationService.findById(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@Valid @RequestBody ReservationRequest reservationRequest) {
        try {
            BooleanRequest booleanRequest = reservationService.save(reservationRequest);
            if (booleanRequest.bool()){

                return new ResponseEntity<>(booleanRequest.message(), HttpStatus.CREATED);
            }
            return new ResponseEntity<>(booleanRequest.message(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<ReservationDto> update(@Valid @RequestBody ReservationRequest reservationRequest, @PathVariable long id) {
        try {
            ReservationDto reservationDto = ReservationDto.toDto(reservationService.update(reservationRequest, id));
            return new ResponseEntity<>(reservationDto,HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable long id) {
        try {
            reservationService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/findAll/active")
    public ResponseEntity<List<ReservationDto>> findAllActive() {
        try {
            List<ReservationDto> reservationDtos = reservationService.findAllActive()
                    .stream()
                    .map(ReservationDto :: toDto)
                    .toList();
            return ResponseEntity.ok(reservationDtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findByCustomer/{customerId}")
    public ResponseEntity<List<ReservationDto>> findByCustomer(@PathVariable long customerId) {
        try {
            List<ReservationDto> reservationDtos = reservationService.findByCustomer(customerId)
                    .stream()
                    .map(ReservationDto :: toDto)
                    .toList();
            return ResponseEntity.ok(reservationDtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findByStatus/{status}")
    public ResponseEntity<List<ReservationDto>> findByStatus(@PathVariable Status status) {
        try {
            List<ReservationDto> reservationDtos = reservationService.findByStatus(status)
                    .stream()
                    .map(ReservationDto :: toDto)
                    .toList();
            return ResponseEntity.ok(reservationDtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
