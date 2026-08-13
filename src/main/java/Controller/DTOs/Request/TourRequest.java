package Controller.DTOs.Request;

import Entity.Enum.CountryTour;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TourRequest {
    private double price;
    private CountryTour countryTour;
    private Long kmOftour;
    private  String nameOfTour;
    private  String locations;
}
