package Controller.DTOs;

import Entity.Enum.CountryTour;
import Entity.Tour;


public record TourDto(double price , CountryTour countryTour , Long kmOftour , String nameOfTour , String locations) {
    public static TourDto toDto (Tour tour){
        return new TourDto(tour.getPrice(),tour.getCountryTour(), tour.getKmOftour(), tour.getNameOfTour(), tour.getLocations());
    }
}
