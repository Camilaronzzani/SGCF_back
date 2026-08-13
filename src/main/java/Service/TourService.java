package Service;


import Controller.DTOs.Request.TourRequest;
import Controller.DTOs.TourDto;

import Entity.Enum.CountryTour;

import Entity.Tour;
import Repositories.TourRepository;
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

    public List<TourDto> findAll() {
        try {
            List<Tour> tourList = tourRepository.findAll();
            List<TourDto> tourDtos = new ArrayList<>();
            tourList.forEach(tour -> {
                TourDto tourDto = TourDto.toDto(tour);
                tourDtos.add(tourDto);
            });
            return tourDtos;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }

    public TourDto findById(long id) {
        try {
            Optional<Tour> tour = Optional.of(tourRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "tour no find")));
            return TourDto.toDto(tour.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        try {
            Tour tour = toTour(tourRequest);
            Tour tourOld = Optional.of(tourRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "tour no find"))).get();
            changeDataByTour(tourOld , tour);
            tourRepository.save(tourOld);
            return "tour: " + tourOld.getNameOfTour() + " save successful ";

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String delete(long id) {
        try {
            Tour tour = tourRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "tour no find"));
            tour.setActive(false);
            tourRepository.save(tour);
            return "Tour: " + tour.getNameOfTour() + " delete successful ";

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
                TourDto tourDto = TourDto.toDto(tour);
                tourDtoList.add(tourDto);
            });
            return tourDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
