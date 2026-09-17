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
        // mudar aqui: tirar o try/catch, o ApiExceptionHandler ja trata (exemplo em TourController.findById)
        try {
            return ResponseEntity.ok(tourService.findAll());
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    // EXEMPLO ControllerAdvice: sem try/catch o erro sobe e o ApiExceptionHandler responde o status certo
    @GetMapping("/findId/{id}")
    public ResponseEntity<TourDto> findById(@PathVariable long id){
        return ResponseEntity.ok(toDto(tourService.findById(id)));
    }

    @PostMapping("/save")
    public ResponseEntity save(@Valid @RequestBody TourRequest tourRequest){
        // mudar aqui: tirar o try/catch, o ApiExceptionHandler ja trata (exemplo em TourController.findById)
        try {
            tourService.save(tourRequest);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TourDto> update(@Valid @RequestBody TourRequest tourRequest, @PathVariable long id){
        // mudar aqui: tirar o try/catch, o ApiExceptionHandler ja trata (exemplo em TourController.findById)
        try {
            TourDto tourDto = TourDto.toDto(tourService.update(tourRequest , id));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ResponseStatusException err) {
            throw err;
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable long id){
        // mudar aqui: tirar o try/catch, o ApiExceptionHandler ja trata (exemplo em TourController.findById)
        try {
            tourService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ResponseStatusException err) {
            throw err;
        } catch (Exception err) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findAll/active")
    public ResponseEntity<List<TourDto>> findAllActive (){
        // mudar aqui: tirar o try/catch, o ApiExceptionHandler ja trata (exemplo em TourController.findById)
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
