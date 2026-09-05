package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller;


import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.TourRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.TourDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Tour;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.TourService;
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
        try {
            return ResponseEntity.ok(tourService.findAll());
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findId/{id}")
    public ResponseEntity<TourDto> findById(@PathVariable long id){
        try {
            return ResponseEntity.ok(toDto(tourService.findById(id)));
        } catch (ResponseStatusException err) {
            throw err;
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody TourRequest tourRequest){
        try {
            return new ResponseEntity<>(tourService.save(tourRequest), HttpStatus.NO_CONTENT);
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<String> update(@RequestBody TourRequest tourRequest, @PathVariable long id){
        try {
            return new ResponseEntity<>(tourService.update(tourRequest , id),HttpStatus.NO_CONTENT);
        } catch (ResponseStatusException err) {
            throw err;
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable long id){
        try {
            return new ResponseEntity<>(tourService.delete(id), HttpStatus.NO_CONTENT);
        } catch (ResponseStatusException err) {
            throw err;
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<String> updatePartial(@PathVariable long id , @RequestBody Map<String , Object> tour){
        try {

            String message = tourService.applyPartialUpdate(id , tour);
            return new ResponseEntity<>(message,HttpStatus.NO_CONTENT);

        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findAll/active")
    public ResponseEntity<List<TourDto>> findAllActive (){
        try {
            List<TourDto> tourDtos = tourService.findAllActive()
                    .stream()
                    .map(TourDto :: toDto)
                    .toList();
            return ResponseEntity.ok(tourDtos);
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }
}
