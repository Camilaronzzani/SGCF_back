package sgcf_back.camilaronzzani.com.github.sgcf_back.service;


import sgcf_back.camilaronzzani.com.github.sgcf_back.exception.ServiceException;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.TourRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Tour;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.ReservationRepository;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.TourRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.TourRequest.toTour;

@Slf4j
@Service
public class TourService {

    private static final String TOUR_NOT_FOUND = "tour not found";
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
            throw new ServiceException(e);
        }

    }

    public Tour findById(long id) {
        try {

            Tour tour = tourRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, TOUR_NOT_FOUND));

            log.info("Tour {} found successfully", id);
            return tour;

        } catch (Exception e) {
            log.error("Error in TourService.findById", e);
            throw new ServiceException(e);
        }
    }

    @Transactional
    public void save(TourRequest tourRequest) {
        try {
            Tour tour = toTour(tourRequest);
            tourRepository.save(tour);

            log.info("Tour {} saved successfully", tour.getNameOfTour());

        } catch (Exception e) {
            log.error("Error in TourService.save", e);
            throw new ServiceException(e);
        }
    }


    public Tour changeDataByTour(long id, TourRequest newTour){
        Tour tourOld = tourRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, TOUR_NOT_FOUND));

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
            throw new ServiceException(e);
        }
    }

    @Transactional
    public void delete(long id) {
        try {

            Tour tour = tourRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, TOUR_NOT_FOUND));
            tour.setActive(false);

            log.info("Tour {} deactivated successfully", tour.getNameOfTour());
        } catch (Exception e) {
            log.error("Error in TourService.delete", e);
            throw new ServiceException(e);
        }
    }

    public List<Tour> findAllActive() {
        try {
            log.info("Fetches tour list active");
            return tourRepository.findByActiveTrue();

        } catch (Exception e) {
            log.error("Error in TourService.findAllActive", e);
            throw new ServiceException(e);
        }
    }
}
