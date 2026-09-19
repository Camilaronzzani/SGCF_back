package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.CountryTour;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Tour;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record TourRequest (

    @Positive
     double price,

     CountryTour countryTour,

    @Positive
     Long kmOftour,

    @NotEmpty
    @NotBlank
      String nameOfTour,

    @NotEmpty

      String locations
){
    public static Tour toTour(TourRequest tourRequest){
        Tour tour = new Tour();
        tour.setCountryTour(tourRequest.countryTour());
        tour.setNameOfTour(tourRequest.nameOfTour());
        tour.setLocations(tourRequest.locations());
        tour.setKmOftour(tourRequest.kmOftour());
        tour.setPrice(tourRequest.price());
        tour.setActive(true);
        return tour;
    }
}
