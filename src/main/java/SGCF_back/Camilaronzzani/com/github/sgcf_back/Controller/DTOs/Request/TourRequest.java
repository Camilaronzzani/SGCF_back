package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.CountryTour;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TourRequest {

    @NotEmpty
    @NotBlank
    private double price;

    @NotEmpty
    @NotBlank
    private CountryTour countryTour;

    @NotEmpty
    @NotBlank
    private Long kmOftour;

    @NotEmpty
    @NotBlank
    private  String nameOfTour;

    @NotEmpty
    @NotBlank
    private  String locations;
}
