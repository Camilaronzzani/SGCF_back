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

import static SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.TourRequest.toTour;

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

    public void save(TourRequest tourRequest) {
        try {
            Tour tour = toTour(tourRequest);
            tourRepository.save(tour);

            log.info("Tour {} saved successfully", tour.getNameOfTour());

        } catch (Exception e) {
            log.error("Error in TourService.save", e);
            throw new RuntimeException(e);
        }
    }


    public Tour changeDataByTour(long id, TourRequest newTour){
        Tour tourOld = tourRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "tour not found"));

        tourOld.setCountryTour(newTour.countryTour());
        tourOld.setNameOfTour(newTour.nameOfTour());
        tourOld.setLocations(newTour.locations());
        tourOld.setKmOftour(newTour.kmOftour());
        tourOld.setPrice(newTour.price());
        return tourOld;
    }

    @Transactional
    public Tour update(TourRequest tourRequest, long id) {
        try {

            Tour tour = changeDataByTour(id, tourRequest);

            log.info("Tour {} update successfully" , tour.getNameOfTour());
            return tour;

        } catch (Exception e) {
            log.error("Error in TourService.update", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void delete(long id) {
        try {

            Tour tour = tourRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "tour not found"));
            tour.setActive(false);

            log.info("Tour {} deactivated successfully", tour.getNameOfTour());
        } catch (Exception e) {
            log.error("Error in TourService.delete", e);
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
