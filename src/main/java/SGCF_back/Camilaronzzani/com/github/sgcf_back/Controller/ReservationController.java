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
            List<ReservationDto> reservationDtos = reservationService.findAll()
                    .stream()
                    .map(ReservationDto :: toDto)
                    .toList();
            return ResponseEntity.ok(reservationDtos);
    }

    @GetMapping("/findId/{id}")
    public ResponseEntity<ReservationDto> findById(@PathVariable long id) {
            return ResponseEntity.ok(ReservationDto.toDto(reservationService.findById(id)));
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@Valid @RequestBody ReservationRequest reservationRequest) {
            BooleanRequest booleanRequest = reservationService.save(reservationRequest);
            if (booleanRequest.bool()){

                return new ResponseEntity<>(booleanRequest.message(), HttpStatus.CREATED);
            }
            return new ResponseEntity<>(booleanRequest.message(), HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ReservationDto> update(@Valid @RequestBody ReservationRequest reservationRequest, @PathVariable long id) {
            ReservationDto reservationDto = ReservationDto.toDto(reservationService.update(reservationRequest, id));
            return ResponseEntity.ok(reservationDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable long id) {
            reservationService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping("/findAll/active")
    public ResponseEntity<List<ReservationDto>> findAllActive() {
            List<ReservationDto> reservationDtos = reservationService.findAllActive()
                    .stream()
                    .map(ReservationDto :: toDto)
                    .toList();
            return ResponseEntity.ok(reservationDtos);
    }

    @GetMapping("/findByCustomer/{customerId}")
    public ResponseEntity<List<ReservationDto>> findByCustomer(@PathVariable long customerId) {
            List<ReservationDto> reservationDtos = reservationService.findByCustomer(customerId)
                    .stream()
                    .map(ReservationDto :: toDto)
                    .toList();
            return ResponseEntity.ok(reservationDtos);
    }

    @GetMapping("/findByStatus/{status}")
    public ResponseEntity<List<ReservationDto>> findByStatus(@PathVariable Status status) {
            List<ReservationDto> reservationDtos = reservationService.findByStatus(status)
                    .stream()
                    .map(ReservationDto :: toDto)
                    .toList();
            return ResponseEntity.ok(reservationDtos);
    }
}
