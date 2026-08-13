package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.CountryTour;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Tour;


public record TourDto(double price , CountryTour countryTour , Long kmOftour , String nameOfTour , String locations) {
    public static TourDto toDto (Tour tour){
        return new TourDto(tour.getPrice(),tour.getCountryTour(), tour.getKmOftour(), tour.getNameOfTour(), tour.getLocations());
    }
}
