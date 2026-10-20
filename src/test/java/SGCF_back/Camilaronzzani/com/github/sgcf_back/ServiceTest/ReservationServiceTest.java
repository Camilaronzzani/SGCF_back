package SGCF_back.Camilaronzzani.com.github.sgcf_back.ServiceTest;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.ReservationRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Customer;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Employee;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Reservation;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Tour;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.CustomerRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.EmployeeRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.ReservationRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.TourRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.ReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
        List<Reservation> reservations = List.of(new Reservation());
        when(reservationRepository.findAll()).thenReturn(reservations);

        assertSame(reservations, reservationService.findAll());
    }

    @Test
    void findByIdReturnsReservationWhenItExists() {
        Reservation reservation = new Reservation();
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        assertSame(reservation, reservationService.findById(1L));
    }

    @Test
    void findByIdWrapsNotFoundErrorWhenReservationDoesNotExist() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reservationService.findById(1L));

        assertWrappedStatus(exception, HttpStatus.NOT_FOUND);
    }

    @Test
    void findAllActiveReturnsActiveReservationsFromRepository() {
        List<Reservation> reservations = List.of(new Reservation());
        when(reservationRepository.findByActiveTrue()).thenReturn(reservations);

        assertSame(reservations, reservationService.findAllActive());
    }

    @Test
    void findByCustomerReturnsCustomerReservationsFromRepository() {
        List<Reservation> reservations = List.of(new Reservation());
        when(reservationRepository.findByCustomerId(7L)).thenReturn(reservations);

        assertSame(reservations, reservationService.findByCustomer(7L));
    }

    @Test
    void findByStatusReturnsReservationsWithRequestedStatus() {
        List<Reservation> reservations = List.of(new Reservation());
        when(reservationRepository.findByStatus(Status.Confirmed)).thenReturn(reservations);

        assertSame(reservations, reservationService.findByStatus(Status.Confirmed));
    }

    @Test
    void toReservationMapsRequestAndRelatedEntities() {
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

        Reservation reservation = reservationService.toReservation(request);

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
        stubRelatedEntities(1L, 2L, 3L);
        ReservationRequest request = new ReservationRequest(
                LocalDate.of(2026, 11, 10), 1L, 2L, 3L, null, null);

        Reservation reservation = reservationService.toReservation(request);

        assertEquals(Status.Pending, reservation.getStatus());
        assertNotNull(reservation.getCustomersNotPaying());
        assertTrue(reservation.getCustomersNotPaying().isEmpty());
        assertTrue(reservation.isActive());
    }

    @Test
    void toReservationRejectsMissingTourId() {
        ReservationRequest request = new ReservationRequest(
                LocalDate.now(), null, 2L, 3L, Status.Pending, null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reservationService.toReservation(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(tourRepository, customerRepository, employeeRepository);
    }

    @Test
    void toReservationRejectsMissingCustomerId() {
        when(tourRepository.findById(1L)).thenReturn(Optional.of(new Tour()));
        ReservationRequest request = new ReservationRequest(
                LocalDate.now(), 1L, null, 3L, Status.Pending, null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reservationService.toReservation(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void toReservationRejectsMissingEmployeeId() {
        when(tourRepository.findById(1L)).thenReturn(Optional.of(new Tour()));
        when(customerRepository.findById(2L)).thenReturn(Optional.of(new Customer()));
        ReservationRequest request = new ReservationRequest(
                LocalDate.now(), 1L, 2L, null, Status.Pending, null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reservationService.toReservation(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void toReservationRejectsUnknownRelatedEntity() {
        when(tourRepository.findById(1L)).thenReturn(Optional.empty());
        ReservationRequest request = new ReservationRequest(
                LocalDate.now(), 1L, 2L, 3L, Status.Pending, null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reservationService.toReservation(request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void savePersistsPendingReservationWithCalculatedValue() {
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

        reservationService.save(request);

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

        Reservation updated = reservationService.update(request, 1L);

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
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());
        stubRelatedEntities(1L, 2L, 3L);
        ReservationRequest request = new ReservationRequest(
                LocalDate.now(), 1L, 2L, 3L, Status.Pending, null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reservationService.update(request, 1L));

        assertWrappedStatus(exception, HttpStatus.NOT_FOUND);
    }

    @Test
    void updateStatusChangesReservationStatus() {
        Reservation reservation = new Reservation();
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        Reservation updated = reservationService.updateStatus(1L, Status.Confirmed);

        assertSame(reservation, updated);
        assertEquals(Status.Confirmed, updated.getStatus());
    }

    @Test
    void updateStatusRejectsNullStatus() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reservationService.updateStatus(1L, null));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void updateStatusRejectsUnknownReservation() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reservationService.updateStatus(1L, Status.Confirmed));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void deleteMarksReservationInactiveWithoutDeletingIt() {
        Reservation reservation = new Reservation();
        reservation.setActive(true);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        reservationService.delete(1L);

        assertFalse(reservation.isActive());
        verify(reservationRepository, never()).delete(any());
    }

    @Test
    void deleteWrapsNotFoundErrorWhenReservationDoesNotExist() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reservationService.delete(1L));

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
