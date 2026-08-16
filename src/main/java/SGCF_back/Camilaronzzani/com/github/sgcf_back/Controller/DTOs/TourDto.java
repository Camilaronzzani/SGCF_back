package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.CountryTour;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Tour;

public record TourDto(long id, double price, CountryTour countryTour, Long kmOftour, String nameOfTour, String locations,
                      long reservationCount) {
    public static TourDto toDto(Tour tour, long reservationCount) {
        return new TourDto(tour.getId(), tour.getPrice(), tour.getCountryTour(), tour.getKmOftour(),
                tour.getNameOfTour(), tour.getLocations(), reservationCount);
    }

    public static TourDto toDto(Tour tour) {
        return toDto(tour, 0);
    }
}
