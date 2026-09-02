package SGCF_back.Camilaronzzani.com.github.sgcf_back.Service;


import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.TourRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.TourDto;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.CountryTour;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Tour;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.ReservationRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.TourRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TourService {
    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public List<Tour> findAll() {
        try {
            log.info("Fetches tour list");
            return tourRepository.findAll();

        } catch (RuntimeException e) {
            log.error("Error in TourService.findAll", e);
            throw new RuntimeException(e);
        }

    }

    public Tour findById(long id) {
        try {

            Tour tour = tourRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "tour not found"));

            log.info("Tour {} found successfully", id);
            return tour;

        } catch (Exception e) {
            log.error("Error in TourService.findById", e);
            throw new RuntimeException(e);
        }
    }

    public String save(TourRequest tourRequest) {
        try {
            Tour tour = toTour(tourRequest);
            tourRepository.save(tour);

            log.info("Tour {} saved successfully", tour.getNameOfTour());
            return "Tour: " + tour.getNameOfTour()+ " saved successfully ";

        } catch (Exception e) {
            log.error("Error in TourService.save", e);
            throw new RuntimeException(e);
        }
    }
    public Tour toTour(TourRequest tourRequest){
        Tour tour = new Tour();
        tour.setCountryTour(tourRequest.getCountryTour());
        tour.setNameOfTour(tourRequest.getNameOfTour());
        tour.setLocations(tourRequest.getLocations());
        tour.setKmOftour(tourRequest.getKmOftour());
        tour.setPrice(tourRequest.getPrice());
        tour.setActive(true);
        return tour;
    }

    public Tour changeDataByTour(long id, TourRequest newTour){
        Tour tourOld = tourRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "tour not found"));

        tourOld.setCountryTour(newTour.getCountryTour());
        tourOld.setNameOfTour(newTour.getNameOfTour());
        tourOld.setLocations(newTour.getLocations());
        tourOld.setKmOftour(newTour.getKmOftour());
        tourOld.setPrice(newTour.getPrice());
        return tourOld;
    }

    @Transactional
    public String update(TourRequest tourRequest, long id) {
        try {

            Tour tour = changeDataByTour(id, tourRequest);

            log.info("Tour {} update successfully" , tour.getNameOfTour());
            return "tour: " + tour.getNameOfTour() + " save successful ";

        } catch (Exception e) {
            log.error("Error in TourService.update", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public String delete(long id) {
        try {

            Tour tour = tourRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "tour not found"));
            tour.setActive(false);

            log.info("Tour {} deactivated successfully", tour.getNameOfTour());
            return "Tour: " + tour.getNameOfTour() + " deleted successfully ";

        } catch (Exception e) {
            log.error("Error in TourService.delete", e);
            throw new RuntimeException(e);
        }
    }
    //delete
    public String applyPartialUpdate(long id, Map<String, Object> tour) {
        try {
            Tour tour1 = tourRepository.findById(id).orElseThrow(()
                    ->  new ResponseStatusException(HttpStatus.NOT_FOUND, "tour no find"));
            tour.forEach((key , value) ->{
                switch (key){
                    case "price" -> tour1.setPrice((double) value) ;
                    case "countryTour" -> tour1.setCountryTour((CountryTour) value);
                    case "kmOftour" -> tour1.setKmOftour((Long) value);
                    case "nameOfTour" -> tour1.setNameOfTour((String) value);
                    case "locations" -> tour1.setLocations((String) value);
                }
            });
            tourRepository.save(tour1);
            return "tour: " + tour1.getNameOfTour() + " delete successful ";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Tour> findAllActive() {
        try {
            log.info("Fetches tour list active");
            return tourRepository.findByActiveTrue();

        } catch (Exception e) {
            log.error("Error in TourService.findAllActive", e);
            throw new RuntimeException(e);
        }
    }


}
