package sgcf_back.camilaronzzani.com.github.sgcf_back.ServiceTest;

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
        // Given
        List<Tour> tours = List.of(new Tour());
        when(tourRepository.findAll()).thenReturn(tours);

        // When
        List<Tour> result = tourService.findAll();

        // Then
        assertSame(tours, result);
    }

    @Test
    void findByIdReturnsTourWhenItExists() {
        // Given
        Tour tour = new Tour();
        tour.setId(1L);
        tour.setNameOfTour("Amazon Tour");
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));

        // When
        Tour result = tourService.findById(1L);

        // Then
        assertSame(tour, result);
        assertEquals("Amazon Tour", result.getNameOfTour());
    }

    @Test
    void savePersistsTourFromRequest() {
        // Given
        TourRequest request = new TourRequest(
                1500.00,
                CountryTour.Brazil,
                500L,
                "Beach Tour",
                "Rio de Janeiro, Santos"
        );

        // When
        tourService.save(request);

        // Then
        verify(tourRepository).save(argThat(tour ->
                tour.getPrice() == 1500.00
                        && tour.getCountryTour() == CountryTour.Brazil
                        && tour.getKmOftour() == 500L
                        && tour.getNameOfTour().equals("Beach Tour")
                        && tour.getLocations().equals("Rio de Janeiro, Santos")
                        && tour.isActive()));
    }

    @Test
    void updateChangesTourFieldsAndReturnsUpdatedTour() {
        // Given
        Tour existingTour = new Tour();
        existingTour.setId(1L);
        existingTour.setNameOfTour("Old Tour");
        existingTour.setPrice(1000.00);
        existingTour.setCountryTour(CountryTour.Brazil);
        existingTour.setKmOftour(300L);
        existingTour.setLocations("Old Location");
        when(tourRepository.findById(1L)).thenReturn(Optional.of(existingTour));
        TourRequest updateRequest = new TourRequest(
                2000.00,
                CountryTour.Argentina,
                600L,
                "Updated Tour",
                "Buenos Aires, Mendoza"
        );

        // When
        Tour updated = tourService.update(updateRequest, 1L);

        // Then
        assertSame(existingTour, updated);
        assertEquals(2000.00, updated.getPrice());
        assertEquals(CountryTour.Argentina, updated.getCountryTour());
        assertEquals(600L, updated.getKmOftour());
        assertEquals("Updated Tour", updated.getNameOfTour());
        assertEquals("Buenos Aires, Mendoza", updated.getLocations());
    }

    @Test
    void deleteMarksTourInactiveWithoutDeletingIt() {
        // Given
        Tour tour = new Tour();
        tour.setId(1L);
        tour.setActive(true);
        tour.setNameOfTour("Tour to Delete");
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));

        // When
        tourService.delete(1L);

        // Then
        assertFalse(tour.isActive());
        verify(tourRepository, never()).delete(any());
    }

    @Test
    void findAllActiveReturnsActiveToursFromRepository() {
        // Given
        Tour tour1 = new Tour();
        tour1.setActive(true);
        Tour tour2 = new Tour();
        tour2.setActive(true);
        List<Tour> activeTours = List.of(tour1, tour2);
        when(tourRepository.findByActiveTrue()).thenReturn(activeTours);

        // When
        List<Tour> result = tourService.findAllActive();

        // Then
        assertSame(activeTours, result);
        assertEquals(2, result.size());
    }

    @Test
    void changeDataByTourUpdatesAllFields() {
        // Given
        Tour existingTour = new Tour();
        existingTour.setId(1L);
        existingTour.setNameOfTour("Old Name");
        when(tourRepository.findById(1L)).thenReturn(Optional.of(existingTour));
        TourRequest newData = new TourRequest(
                3000.00,
                CountryTour.Paraguay,
                800L,
                "New Name",
                "Asuncion, Ciudad del Este"
        );

        // When
        Tour result = tourService.changeDataByTour(1L, newData);

        // Then
        assertSame(existingTour, result);
        assertEquals(3000.00, result.getPrice());
        assertEquals(CountryTour.Paraguay, result.getCountryTour());
        assertEquals(800L, result.getKmOftour());
        assertEquals("New Name", result.getNameOfTour());
        assertEquals("Asuncion, Ciudad del Este", result.getLocations());
    }

    @Test
    void findByIdWrapsNotFoundErrorWhenTourDoesNotExist() {
        // Given
        when(tourRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tourService.findById(999L));

        // Then
        assertInstanceOf(ResponseStatusException.class, exception.getCause());
        assertEquals(HttpStatus.NOT_FOUND,
                ((ResponseStatusException) exception.getCause()).getStatusCode());
    }

    @Test
    void updateWrapsNotFoundErrorWhenTourDoesNotExist() {
        // Given
        when(tourRepository.findById(999L)).thenReturn(Optional.empty());
        TourRequest request = new TourRequest(
                1500.00,
                CountryTour.Brazil,
                500L,
                "Beach Tour",
                "Rio de Janeiro"
        );

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tourService.update(request, 999L));

        // Then
        assertInstanceOf(ResponseStatusException.class, exception.getCause());
        assertEquals(HttpStatus.NOT_FOUND,
                ((ResponseStatusException) exception.getCause()).getStatusCode());
    }

    @Test
    void deleteWrapsNotFoundErrorWhenTourDoesNotExist() {
        // Given
        when(tourRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tourService.delete(999L));

        // Then
        assertInstanceOf(ResponseStatusException.class, exception.getCause());
        assertEquals(HttpStatus.NOT_FOUND,
                ((ResponseStatusException) exception.getCause()).getStatusCode());
    }

    @Test
    void changeDataByTourThrowsNotFoundWhenTourDoesNotExist() {
        // Given
        when(tourRepository.findById(999L)).thenReturn(Optional.empty());
        TourRequest request = new TourRequest(
                1500.00,
                CountryTour.Brazil,
                500L,
                "Beach Tour",
                "Rio de Janeiro"
        );

        // When / Then
        assertThrows(ResponseStatusException.class,
                () -> tourService.changeDataByTour(999L, request));
    }

    @Test
    void findAllWrapsExceptionWhenRepositoryFails() {
        // Given
        when(tourRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tourService.findAll());

        // Then
        assertEquals("Database error", exception.getCause().getMessage());
    }

    @Test
    void saveWrapsExceptionWhenRepositoryFails() {
        // Given
        when(tourRepository.save(any(Tour.class)))
                .thenThrow(new RuntimeException("Database error"));
        TourRequest request = new TourRequest(
                1500.00,
                CountryTour.Brazil,
                500L,
                "Beach Tour",
                "Rio de Janeiro"
        );

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tourService.save(request));

        // Then
        assertEquals("Database error", exception.getCause().getMessage());
    }

    @Test
    void findAllActiveWrapsExceptionWhenRepositoryFails() {
        // Given
        when(tourRepository.findByActiveTrue())
                .thenThrow(new RuntimeException("Database error"));

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tourService.findAllActive());

        // Then
        assertEquals("Database error", exception.getCause().getMessage());
    }
}
