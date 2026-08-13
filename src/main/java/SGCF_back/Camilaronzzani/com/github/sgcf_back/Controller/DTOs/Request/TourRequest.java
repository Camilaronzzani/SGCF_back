package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.CountryTour;
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
