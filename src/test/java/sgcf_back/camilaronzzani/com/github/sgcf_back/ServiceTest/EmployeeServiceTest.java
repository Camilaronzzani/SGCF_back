package sgcf_back.camilaronzzani.com.github.sgcf_back.service;

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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @Test
    void findAllReturnsAllEmployees() {
        // Given
        when(employeeRepository.findAll()).thenReturn(List.of(new Employee()));

        // When
        List<Employee> resultado = service.findAll();

        // Then
        assertEquals(1, resultado.size());
    }

    @Test
    void findByIdReturnsEmployeeWhenItExists() {
        // Given
        Employee e = new Employee();
        e.setName("Joao");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(e));

        // When
        Employee resultado = service.findById(1L);

        // Then
        assertEquals("Joao", resultado.getName());
    }

    @Test
    void findByIdThrowsWhenEmployeeDoesNotExist() {
        // Given
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.findById(99L));

        // Then
        assertTrue(ex.getCause() instanceof ResponseStatusException);
    }

    @Test
    void saveStoresEmployeeInRepository() {
        // Given
        EmployeeRequest request = mock(EmployeeRequest.class);

        // When
        service.save(request);

        // Then
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void updateChangesEmployeeData() {
        // Given
        Employee e = new Employee();
        e.setName("Antigo");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(e));
        EmployeeRequest request = mock(EmployeeRequest.class);
        when(request.name()).thenReturn("Novo");

        // When
        Employee resultado = service.update(request, 1L);

        // Then
        assertEquals("Novo", resultado.getName());
    }

    @Test
    void deleteDeactivatesEmployee() {
        // Given
        Employee e = new Employee();
        e.setActive(true);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(e));

        // When
        service.delete(1L);

        // Then
        assertFalse(e.isActive());
    }

    @Test
    void findAllActiveReturnsOnlyActiveEmployees() {
        // Given
        when(employeeRepository.findByActiveTrue()).thenReturn(List.of(new Employee()));

        // When
        List<Employee> resultado = service.findAllActive();

        // Then
        assertEquals(1, resultado.size());
    }

    @Test
    void toSummaryDtoBuildsDtoUsingReservationTotals() {
        // Given
        Employee e = new Employee();
        e.setId(1L);
        e.setName("Joao");

        // When
        EmployeDto dto = service.toSummaryDto(e);

        // Then
        assertNotNull(dto);
        verify(reservationRepository).countByEmployeeIdAndActiveTrue(1L);
        verify(reservationRepository).sumValueByEmployeeAndStatus(1L, Status.Confirmed);
    }
}
