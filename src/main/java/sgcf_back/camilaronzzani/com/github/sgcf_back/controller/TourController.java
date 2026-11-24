package sgcf_back.camilaronzzani.com.github.sgcf_back.controller;

import org.springframework.security.access.prepost.PreAuthorize;


import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.TourRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.TourDto;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Tour;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.TourService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.TourDto.toDto;

@RestController
@RequestMapping("api/tour")
public class TourController {
    @Autowired
    private TourService tourService;

    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")
    @GetMapping("/findAll")
    public ResponseEntity<List<Tour>> findAll(){
            return ResponseEntity.ok(tourService.findAll());
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")
    @GetMapping("/findId/{id}")
    public ResponseEntity<TourDto> findById(@PathVariable long id){
        return ResponseEntity.ok(toDto(tourService.findById(id)));
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")
    @PostMapping("/save")
    public ResponseEntity<Void> save(@Valid @RequestBody TourRequest tourRequest){
            tourService.save(tourRequest);
            return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")
    @PutMapping("/update/{id}")
    public ResponseEntity<TourDto> update(@Valid @RequestBody TourRequest tourRequest, @PathVariable long id){
            TourDto tourDto = TourDto.toDto(tourService.update(tourRequest , id));
            return new ResponseEntity<>(tourDto,HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id){
            tourService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")
    @GetMapping("/findAll/active")
    public ResponseEntity<List<TourDto>> findAllActive (){
            List<TourDto> tourDtos = tourService.findAllActive()
                    .stream()
                    .map(TourDto :: toDto)
                    .toList();
            return ResponseEntity.ok(tourDtos);
    }
}
