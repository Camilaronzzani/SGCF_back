package sgcf_back.camilaronzzani.com.github.sgcf_back.serviceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.TourRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Tour;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.CountryTour;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.ReservationRepository;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.TourRepository;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.TourService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourServiceTest {

    @Mock
    private TourRepository tourRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private TourService tourService;

    @Test
    void findAllReturnsToursFromRepository() {
        List<Tour> tours = List.of(new Tour());
        when(tourRepository.findAll()).thenReturn(tours);

        List<Tour> result = tourService.findAll();

        assertSame(tours, result);
    }

    @Test
    void findByIdReturnsTourWhenItExists() {
        Tour tour = new Tour();
        tour.setId(1L);
        tour.setNameOfTour("Amazon Tour");
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));

        Tour result = tourService.findById(1L);

        assertSame(tour, result);
        assertEquals("Amazon Tour", result.getNameOfTour());
    }

    @Test
    void savePersistsTourFromRequest() {
        TourRequest request = new TourRequest(
                1500.00,
                CountryTour.BRAZIL,
                500L,
                "Beach Tour",
                "Rio de Janeiro, Santos"
        );

        tourService.save(request);

        verify(tourRepository).save(argThat(tour ->
                tour.getPrice() == 1500.00
                        && tour.getCountryTour() == CountryTour.BRAZIL
                        && tour.getKmOftour() == 500L
                        && tour.getNameOfTour().equals("Beach Tour")
                        && tour.getLocations().equals("Rio de Janeiro, Santos")
                        && tour.isActive()));
    }

    @Test
    void updateChangesTourFieldsAndReturnsUpdatedTour() {
        Tour existingTour = new Tour();
        existingTour.setId(1L);
        existingTour.setNameOfTour("Old Tour");
        existingTour.setPrice(1000.00);
        existingTour.setCountryTour(CountryTour.BRAZIL);
        existingTour.setKmOftour(300L);
        existingTour.setLocations("Old Location");
        when(tourRepository.findById(1L)).thenReturn(Optional.of(existingTour));
        TourRequest updateRequest = new TourRequest(
                2000.00,
                CountryTour.ARGENTINA,
                600L,
                "Updated Tour",
                "Buenos Aires, Mendoza"
        );

        Tour updated = tourService.update(updateRequest, 1L);

        assertSame(existingTour, updated);
        assertEquals(2000.00, updated.getPrice());
        assertEquals(CountryTour.ARGENTINA, updated.getCountryTour());
        assertEquals(600L, updated.getKmOftour());
        assertEquals("Updated Tour", updated.getNameOfTour());
        assertEquals("Buenos Aires, Mendoza", updated.getLocations());
    }

    @Test
    void deleteMarksTourInactiveWithoutDeletingIt() {
        Tour tour = new Tour();
        tour.setId(1L);
        tour.setActive(true);
        tour.setNameOfTour("Tour to Delete");
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));

        tourService.delete(1L);

        assertFalse(tour.isActive());
        verify(tourRepository, never()).delete(any());
    }

    @Test
    void findAllActiveReturnsActiveToursFromRepository() {
        Tour tour1 = new Tour();
        tour1.setActive(true);
        Tour tour2 = new Tour();
        tour2.setActive(true);
        List<Tour> activeTours = List.of(tour1, tour2);
        when(tourRepository.findByActiveTrue()).thenReturn(activeTours);

        List<Tour> result = tourService.findAllActive();

        assertSame(activeTours, result);
        assertEquals(2, result.size());
    }

    @Test
    void changeDataByTourUpdatesAllFields() {
        Tour existingTour = new Tour();
        existingTour.setId(1L);
        existingTour.setNameOfTour("Old Name");
        when(tourRepository.findById(1L)).thenReturn(Optional.of(existingTour));
        TourRequest newData = new TourRequest(
                3000.00,
                CountryTour.PARAGUAY,
                800L,
                "New Name",
                "Asuncion, Ciudad del Este"
        );

        Tour result = tourService.changeDataByTour(1L, newData);

        assertSame(existingTour, result);
        assertEquals(3000.00, result.getPrice());
        assertEquals(CountryTour.PARAGUAY, result.getCountryTour());
        assertEquals(800L, result.getKmOftour());
        assertEquals("New Name", result.getNameOfTour());
        assertEquals("Asuncion, Ciudad del Este", result.getLocations());
    }

    @Test
    void findByIdWrapsNotFoundErrorWhenTourDoesNotExist() {
        when(tourRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tourService.findById(999L));

        assertInstanceOf(ResponseStatusException.class, exception.getCause());
        assertEquals(HttpStatus.NOT_FOUND,
                ((ResponseStatusException) exception.getCause()).getStatusCode());
    }

    @Test
    void updateWrapsNotFoundErrorWhenTourDoesNotExist() {
        when(tourRepository.findById(999L)).thenReturn(Optional.empty());
        TourRequest request = new TourRequest(
                1500.00,
                CountryTour.BRAZIL,
                500L,
                "Beach Tour",
                "Rio de Janeiro"
        );

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tourService.update(request, 999L));

        assertInstanceOf(ResponseStatusException.class, exception.getCause());
        assertEquals(HttpStatus.NOT_FOUND,
                ((ResponseStatusException) exception.getCause()).getStatusCode());
    }

    @Test
    void deleteWrapsNotFoundErrorWhenTourDoesNotExist() {
        when(tourRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tourService.delete(999L));

        assertInstanceOf(ResponseStatusException.class, exception.getCause());
        assertEquals(HttpStatus.NOT_FOUND,
                ((ResponseStatusException) exception.getCause()).getStatusCode());
    }

    @Test
    void changeDataByTourThrowsNotFoundWhenTourDoesNotExist() {
        when(tourRepository.findById(999L)).thenReturn(Optional.empty());
        TourRequest request = new TourRequest(
                1500.00,
                CountryTour.BRAZIL,
                500L,
                "Beach Tour",
                "Rio de Janeiro"
        );

        assertThrows(ResponseStatusException.class,
                () -> tourService.changeDataByTour(999L, request));
    }

    @Test
    void findAllWrapsExceptionWhenRepositoryFails() {
        when(tourRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tourService.findAll());

        assertEquals("Database error", exception.getCause().getMessage());
    }

    @Test
    void saveWrapsExceptionWhenRepositoryFails() {
        when(tourRepository.save(any(Tour.class)))
                .thenThrow(new RuntimeException("Database error"));
        TourRequest request = new TourRequest(
                1500.00,
                CountryTour.BRAZIL,
                500L,
                "Beach Tour",
                "Rio de Janeiro"
        );

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tourService.save(request));

        assertEquals("Database error", exception.getCause().getMessage());
    }

    @Test
    void findAllActiveWrapsExceptionWhenRepositoryFails() {
        when(tourRepository.findByActiveTrue())
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tourService.findAllActive());

        assertEquals("Database error", exception.getCause().getMessage());
    }
}
