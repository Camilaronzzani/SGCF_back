package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller;


import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.TourRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.TourDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/Tour")
@CrossOrigin(origins = "http://localhost:4200")
public class TourController {
    @Autowired
    private TourService tourService;
    @GetMapping("/findAll")
    public ResponseEntity<List<TourDto>> findAll(){
        try {
            return ResponseEntity.ok(tourService.findAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findId/{id}")
    public ResponseEntity<TourDto> findById(@PathVariable long id){
        try {
            return ResponseEntity.ok(tourService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody TourRequest tourRequest){
        try {
            return ResponseEntity.ok(tourService.save(tourRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<String> update(@RequestBody TourRequest tourRequest, @PathVariable long id){
        try {
            return ResponseEntity.ok(tourService.update(tourRequest , id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable long id){
        try {
            return ResponseEntity.ok(tourService.delete(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/updatePatch/{id}")
    public ResponseEntity<String> updatePartial(@PathVariable long id , @RequestBody Map<String , Object> tour){
        try {

            String message = tourService.applyPartialUpdate(id , tour);
            return ResponseEntity.ok(message);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findAll/active")
    public ResponseEntity<List<TourDto>> findAllActive (){
        try {
            return ResponseEntity.ok(tourService.findAllActive());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
