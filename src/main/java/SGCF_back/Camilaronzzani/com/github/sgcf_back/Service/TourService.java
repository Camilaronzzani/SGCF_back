package SGCF_back.Camilaronzzani.com.github.sgcf_back.Service;


import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.TourRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.TourDto;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.CountryTour;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Tour;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.ReservationRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TourService {
    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public List<TourDto> findAll() {
        try {
            List<Tour> tourList = tourRepository.findAll();
            List<TourDto> tourDtos = new ArrayList<>();
            tourList.forEach(tour -> {
                TourDto tourDto = toDto(tour);
                tourDtos.add(tourDto);
            });
            return tourDtos;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }

    public TourDto findById(long id) {
        Tour tour = tourRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "tour no find"));
        return toDto(tour);
    }

    public String save(TourRequest tourRequest) {
        try {
            Tour tour = toTour(tourRequest);
            tourRepository.save(tour);
            return "Tour: " + tour.getNameOfTour()+ " save successful ";
        } catch (Exception e) {
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

    public void changeDataByTour(Tour tourOld, Tour newTour){
        tourOld.setCountryTour(newTour.getCountryTour());
        tourOld.setNameOfTour(newTour.getNameOfTour());
        tourOld.setLocations(newTour.getLocations());
        tourOld.setKmOftour(newTour.getKmOftour());
        tourOld.setPrice(newTour.getPrice());
    }

    public String update(TourRequest tourRequest, long id) {
        Tour tour = toTour(tourRequest);
        Tour tourOld = tourRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "tour no find"));
        changeDataByTour(tourOld, tour);
        tourRepository.save(tourOld);
        return "tour: " + tourOld.getNameOfTour() + " save successful ";
    }


    public String delete(long id) {
        Tour tour = tourRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "tour no find"));
        tour.setActive(false);
        tourRepository.save(tour);
        return "Tour: " + tour.getNameOfTour() + " delete successful ";
    }

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

    public List<TourDto> findAllActive() {
        try {
            List<Tour> tourList = tourRepository.findByActiveTrue();
            List<TourDto> tourDtoList = new ArrayList<>();
            tourList.forEach(tour -> {
                TourDto tourDto = toDto(tour);
                tourDtoList.add(tourDto);
            });
            return tourDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private TourDto toDto(Tour tour) {
        return TourDto.toDto(tour, reservationRepository.countByTour_IdAndActiveTrue(tour.getId()));
    }
}
