package Controller.DTOs;

import Entity.Enum.CountryTour;
import Entity.Tour;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record TourDto(double price , CountryTour countryTour , Long kmOftour , String nameOfTour , String locations) {
    public static TourDto tourDto (Tour tour){
        return new TourDto(tour.getPrice(),tour.getCountryTour(), tour.getKmOftour(), tour.getNameOfTour(), tour.getLocations());
    }
}
