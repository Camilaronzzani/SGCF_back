package sgcf_back.camilaronzzani.com.github.sgcf_back.ServiceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.ReservationRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Customer;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Employee;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Reservation;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Tour;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Status;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.CustomerRepository;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.EmployeeRepository;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.ReservationRepository;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.TourRepository;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.ReservationService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private TourRepository tourRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void findAllReturnsReservationsFromRepository() {
        // Given
        List<Reservation> reservations = List.of(new Reservation());
        when(reservationRepository.findAll()).thenReturn(reservations);

        // When
        List<Reservation> result = reservationService.findAll();

        // Then
        assertSame(reservations, result);
    }

    @Test
    void findByIdReturnsReservationWhenItExists() {
        // Given
        Reservation reservation = new Reservation();
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // When
        Reservation result = reservationService.findById(1L);

        // Then
        assertSame(reservation, result);
    }

    @Test
    void findByIdWrapsNotFoundErrorWhenReservationDoesNotExist() {
        // Given
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reservationService.findById(1L));

        // Then
        assertWrappedStatus(exception, HttpStatus.NOT_FOUND);
    }

    @Test
    void findAllActiveReturnsActiveReservationsFromRepository() {
        // Given
        List<Reservation> reservations = List.of(new Reservation());
        when(reservationRepository.findByActiveTrue()).thenReturn(reservations);

        // When
        List<Reservation> result = reservationService.findAllActive();

        // Then
        assertSame(reservations, result);
    }

    @Test
    void findByCustomerReturnsCustomerReservationsFromRepository() {
        // Given
        List<Reservation> reservations = List.of(new Reservation());
        when(reservationRepository.findByCustomerId(7L)).thenReturn(reservations);

        // When
        List<Reservation> result = reservationService.findByCustomer(7L);

        // Then
        assertSame(reservations, result);
    }

    @Test
    void findByStatusReturnsReservationsWithRequestedStatus() {
        // Given
        List<Reservation> reservations = List.of(new Reservation());
        when(reservationRepository.findByStatus(Status.Confirmed)).thenReturn(reservations);

        // When
        List<Reservation> result = reservationService.findByStatus(Status.Confirmed);

        // Then
        assertSame(reservations, result);
    }

    @Test
    void toReservationMapsRequestAndRelatedEntities() {
        // Given
        Tour tour = new Tour();
        Customer customer = new Customer();
        Employee employee = new Employee();
        Customer nonPayingCustomer = new Customer();
        tour.setPrice(125.50);
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(customerRepository.findById(2L)).thenReturn(Optional.of(customer));
        when(employeeRepository.findById(3L)).thenReturn(Optional.of(employee));
        when(customerRepository.findById(4L)).thenReturn(Optional.of(nonPayingCustomer));
        ReservationRequest request = new ReservationRequest(
                LocalDate.of(2026, 11, 10), 1L, 2L, 3L,
                Status.Confirmed, List.of(4L));

        // When
        Reservation reservation = reservationService.toReservation(request);

        // Then
        assertEquals(request.date(), reservation.getDate());
        assertSame(tour, reservation.getTour());
        assertSame(customer, reservation.getCustomer());
        assertSame(employee, reservation.getEmployee());
        assertEquals(Status.Confirmed, reservation.getStatus());
        assertEquals(List.of(nonPayingCustomer), reservation.getCustomersNotPaying());
        assertTrue(reservation.isActive());
    }

    @Test
    void toReservationDefaultsStatusAndNonPayingCustomers() {
        // Given
        stubRelatedEntities(1L, 2L, 3L);
        ReservationRequest request = new ReservationRequest(
                LocalDate.of(2026, 11, 10), 1L, 2L, 3L, null, null);

        // When
        Reservation reservation = reservationService.toReservation(request);

        // Then
        assertEquals(Status.Pending, reservation.getStatus());
        assertNotNull(reservation.getCustomersNotPaying());
        assertTrue(reservation.getCustomersNotPaying().isEmpty());
        assertTrue(reservation.isActive());
    }

    @Test
    void toReservationRejectsMissingTourId() {
        // Given
        ReservationRequest request = new ReservationRequest(
                LocalDate.now(), null, 2L, 3L, Status.Pending, null);

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reservationService.toReservation(request));

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(tourRepository, customerRepository, employeeRepository);
    }

    @Test
    void toReservationRejectsMissingCustomerId() {
        // Given
        when(tourRepository.findById(1L)).thenReturn(Optional.of(new Tour()));
        ReservationRequest request = new ReservationRequest(
                LocalDate.now(), 1L, null, 3L, Status.Pending, null);

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reservationService.toReservation(request));

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void toReservationRejectsMissingEmployeeId() {
        // Given
        when(tourRepository.findById(1L)).thenReturn(Optional.of(new Tour()));
        when(customerRepository.findById(2L)).thenReturn(Optional.of(new Customer()));
        ReservationRequest request = new ReservationRequest(
                LocalDate.now(), 1L, 2L, null, Status.Pending, null);

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reservationService.toReservation(request));

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void toReservationRejectsUnknownRelatedEntity() {
        // Given
        when(tourRepository.findById(1L)).thenReturn(Optional.empty());
        ReservationRequest request = new ReservationRequest(
                LocalDate.now(), 1L, 2L, 3L, Status.Pending, null);

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reservationService.toReservation(request));

        // Then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void savePersistsPendingReservationWithCalculatedValue() {
        // Given
        Tour tour = new Tour();
        tour.setPrice(200.0);
        Customer customer = new Customer();
        customer.setName("Main customer");
        Employee employee = new Employee();
        Customer nonPayingCustomer = new Customer();
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(customerRepository.findById(2L)).thenReturn(Optional.of(customer));
        when(employeeRepository.findById(3L)).thenReturn(Optional.of(employee));
        when(customerRepository.findById(4L)).thenReturn(Optional.of(nonPayingCustomer));
        when(reservationRepository.saveAndFlush(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        ReservationRequest request = new ReservationRequest(
                LocalDate.of(2026, 11, 10), 1L, 2L, 3L,
                Status.Confirmed, List.of(4L));

        // When
        reservationService.save(request);

        // Then
        verify(reservationRepository).saveAndFlush(argThat(reservation ->
                reservation.getTour() == tour
                        && reservation.getCustomer() == customer
                        && reservation.getEmployee() == employee
                        && reservation.getStatus() == Status.Pending
                        && reservation.getValue() == 400.0
                        && reservation.isActive()));
    }

    @Test
    void updateChangesReservationFieldsAndRecalculatesValue() {
        // Given
        Reservation existing = new Reservation();
        Tour tour = new Tour();
        tour.setPrice(150.0);
        Customer customer = new Customer();
        Employee employee = new Employee();
        Customer nonPayingCustomer = new Customer();
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(customerRepository.findById(2L)).thenReturn(Optional.of(customer));
        when(employeeRepository.findById(3L)).thenReturn(Optional.of(employee));
        when(customerRepository.findById(4L)).thenReturn(Optional.of(nonPayingCustomer));
        ReservationRequest request = new ReservationRequest(
                LocalDate.of(2026, 12, 1), 1L, 2L, 3L,
                Status.Confirmed, List.of(4L));

        // When
        Reservation updated = reservationService.update(request, 1L);

        // Then
        assertSame(existing, updated);
        assertEquals(request.date(), updated.getDate());
        assertSame(tour, updated.getTour());
        assertSame(customer, updated.getCustomer());
        assertSame(employee, updated.getEmployee());
        assertEquals(Status.Confirmed, updated.getStatus());
        assertEquals(300.0, updated.getValue());
    }

    @Test
    void updateWrapsNotFoundErrorWhenReservationDoesNotExist() {
        // Given
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());
        stubRelatedEntities(1L, 2L, 3L);
        ReservationRequest request = new ReservationRequest(
                LocalDate.now(), 1L, 2L, 3L, Status.Pending, null);

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reservationService.update(request, 1L));

        // Then
        assertWrappedStatus(exception, HttpStatus.NOT_FOUND);
    }

    @Test
    void updateStatusChangesReservationStatus() {
        // Given
        Reservation reservation = new Reservation();
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // When
        Reservation updated = reservationService.updateStatus(1L, Status.Confirmed);

        // Then
        assertSame(reservation, updated);
        assertEquals(Status.Confirmed, updated.getStatus());
    }

    @Test
    void updateStatusRejectsNullStatus() {
        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reservationService.updateStatus(1L, null));

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void updateStatusRejectsUnknownReservation() {
        // Given
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reservationService.updateStatus(1L, Status.Confirmed));

        // Then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void deleteMarksReservationInactiveWithoutDeletingIt() {
        // Given
        Reservation reservation = new Reservation();
        reservation.setActive(true);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // When
        reservationService.delete(1L);

        // Then
        assertFalse(reservation.isActive());
        verify(reservationRepository, never()).delete(any());
    }

    @Test
    void deleteWrapsNotFoundErrorWhenReservationDoesNotExist() {
        // Given
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reservationService.delete(1L));

        // Then
        assertWrappedStatus(exception, HttpStatus.NOT_FOUND);
    }

    private void stubRelatedEntities(long tourId, long customerId, long employeeId) {
        when(tourRepository.findById(tourId)).thenReturn(Optional.of(new Tour()));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(new Customer()));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(new Employee()));
    }

    private static void assertWrappedStatus(RuntimeException exception, HttpStatus status) {
        assertInstanceOf(ResponseStatusException.class, exception.getCause());
        assertEquals(status, ((ResponseStatusException) exception.getCause()).getStatusCode());
    }
}
