package sgcf_back.camilaronzzani.com.github.sgcf_back.serviceTest;

import org.springframework.http.HttpStatus;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Language;
import sgcf_back.camilaronzzani.com.github.sgcf_back.exception.ServiceException;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.EmployeDto;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.EmployeeRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Employee;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Status;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.EmployeeRepository;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.ReservationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private EmployeeService service;

    private EmployeeRequest createValidRequest() {
        return new EmployeeRequest(
                "12345678909",
                "João Silva",
                List.of(Language.PORTUGUESE),
                LocalDate.of(1990, 1, 1)
        );
    }

    @Test
    void findAllReturnsAllEmployees() {
        when(employeeRepository.findAll()).thenReturn(List.of(new Employee()));

        List<Employee> resultado = service.findAll();

        assertEquals(1, resultado.size());
    }

    @Test
    void findByIdReturnsEmployeeWhenItExists() {
        Employee e = new Employee();
        e.setName("Joao");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(e));

        Employee resultado = service.findById(1L);

        assertEquals("Joao", resultado.getName());
    }

    @Test
    void findByIdThrowsWhenEmployeeDoesNotExist() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.findById(99L));

        assertInstanceOf(ResponseStatusException.class, ex.getCause());
    }

    @Test
    void saveStoresEmployeeInRepository() {
        EmployeeRequest request = mock(EmployeeRequest.class);

        service.save(request);

        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void updateChangesEmployeeData() {
        Employee e = new Employee();
        e.setName("Antigo");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(e));
        EmployeeRequest request = mock(EmployeeRequest.class);
        when(request.name()).thenReturn("Novo");

        Employee resultado = service.update(request, 1L);

        assertEquals("Novo", resultado.getName());
    }

    @Test
    void deleteDeactivatesEmployee() {
        Employee e = new Employee();
        e.setActive(true);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(e));

        service.delete(1L);

        assertFalse(e.isActive());
    }

    @Test
    void findAllActiveReturnsOnlyActiveEmployees() {
        when(employeeRepository.findByActiveTrue()).thenReturn(List.of(new Employee()));

        List<Employee> resultado = service.findAllActive();

        assertEquals(1, resultado.size());
    }

    @Test
    void toSummaryDtoBuildsDtoUsingReservationTotals() {
        Employee e = new Employee();
        e.setId(1L);
        e.setName("Joao");

        EmployeDto dto = service.toSummaryDto(e);

        assertNotNull(dto);
        verify(reservationRepository).countByEmployeeIdAndActiveTrue(1L);
        verify(reservationRepository).sumValueByEmployeeAndStatus(1L, Status.CONFIRMED);
    }

    @Test
    void findAllThrowsServiceExceptionWhenRepositoryFails() {
        when(employeeRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        assertThrows(ServiceException.class, () -> service.findAll());
    }

    @Test
    void findByIdThrowsServiceExceptionWhenEmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> service.findById(1L));
        assertInstanceOf(ResponseStatusException.class, exception.getCause());
        assertEquals(HttpStatus.NOT_FOUND, ((ResponseStatusException) exception.getCause()).getStatusCode());
    }

    @Test
    void findByIdThrowsServiceExceptionWhenRepositoryFails() {
        when(employeeRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(ServiceException.class, () -> service.findById(1L));
    }

    @Test
    void saveThrowsServiceExceptionWhenRepositoryFails() {
        EmployeeRequest request = createValidRequest();
        when(employeeRepository.save(any(Employee.class))).thenThrow(new RuntimeException("Save error"));

        assertThrows(ServiceException.class, () -> service.save(request));
    }

    @Test
    void updateThrowsServiceExceptionWhenEmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());
        EmployeeRequest request = createValidRequest();

        ServiceException exception = assertThrows(ServiceException.class, () -> service.update(request, 1L));
        assertInstanceOf(ResponseStatusException.class, exception.getCause());
        assertEquals(HttpStatus.NOT_FOUND, ((ResponseStatusException) exception.getCause()).getStatusCode());
    }

    @Test
    void updateThrowsServiceExceptionWhenRepositoryFails() {
        when(employeeRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));
        EmployeeRequest request = createValidRequest();

        assertThrows(ServiceException.class, () -> service.update(request, 1L));
    }

    @Test
    void deleteThrowsServiceExceptionWhenEmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> service.delete(1L));
        assertInstanceOf(ResponseStatusException.class, exception.getCause());
        assertEquals(HttpStatus.NOT_FOUND, ((ResponseStatusException) exception.getCause()).getStatusCode());
    }

    @Test
    void deleteThrowsServiceExceptionWhenRepositoryFails() {
        when(employeeRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(ServiceException.class, () -> service.delete(1L));
    }

    @Test
    void findAllActiveThrowsServiceExceptionWhenRepositoryFails() {
        when(employeeRepository.findByActiveTrue()).thenThrow(new RuntimeException("Database error"));

        assertThrows(ServiceException.class, () -> service.findAllActive());
    }
}
