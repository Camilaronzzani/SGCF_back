package sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Tour;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.CountryTour;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TourRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private TourRequest createValidRequest() {
        return new TourRequest(150.0, CountryTour.values()[0], 20L, "Cataratas", "Foz do Iguaçu");
    }

    @Test
    void deveMapearRequestParaTour() {
        TourRequest request = createValidRequest();

        Tour tour = TourRequest.toTour(request);

        assertEquals(150.0, tour.getPrice(), 0.001);
        assertEquals(request.countryTour(), tour.getCountryTour());
        assertEquals(20L, tour.getKmOftour());
        assertEquals("Cataratas", tour.getNameOfTour());
        assertEquals("Foz do Iguaçu", tour.getLocations());
        assertTrue(tour.isActive());
    }

    @Test
    void shouldAcceptValidRequest() {
        assertTrue(validator.validate(createValidRequest()).isEmpty());
    }

    @Test
    void mustRejectPriceZeroOrNegativ() {
        TourRequest request = new TourRequest(0.0, CountryTour.values()[0], 20L, "Cataratas", "Foz");

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shouldRejectBlankName() {
        TourRequest request = new TourRequest(150.0, CountryTour.values()[0], 20L, "   ", "Foz");

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shouldRejectEmptyLocations() {
        TourRequest request = new TourRequest(150.0, CountryTour.values()[0], 20L, "Cataratas", "");

        assertFalse(validator.validate(request).isEmpty());
    }
}