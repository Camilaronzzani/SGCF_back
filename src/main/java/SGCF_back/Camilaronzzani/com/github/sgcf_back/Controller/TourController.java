package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller;


import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.TourRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.TourDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Tour;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.TourService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.TourDto.toDto;

@RestController
@RequestMapping("api/tour")
public class TourController {
    @Autowired
    private TourService tourService;

    @GetMapping("/findAll")
    public ResponseEntity<List<Tour>> findAll(){
            return ResponseEntity.ok(tourService.findAll());
    }

    @GetMapping("/findId/{id}")
    public ResponseEntity<TourDto> findById(@PathVariable long id){
        return ResponseEntity.ok(toDto(tourService.findById(id)));
    }

    @PostMapping("/save")
    public ResponseEntity save(@Valid @RequestBody TourRequest tourRequest){
            tourService.save(tourRequest);
            return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TourDto> update(@Valid @RequestBody TourRequest tourRequest, @PathVariable long id){
            TourDto tourDto = TourDto.toDto(tourService.update(tourRequest , id));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable long id){
            tourService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/findAll/active")
    public ResponseEntity<List<TourDto>> findAllActive (){
            List<TourDto> tourDtos = tourService.findAllActive()
                    .stream()
                    .map(TourDto :: toDto)
                    .toList();
            return ResponseEntity.ok(tourDtos);
    }
}
