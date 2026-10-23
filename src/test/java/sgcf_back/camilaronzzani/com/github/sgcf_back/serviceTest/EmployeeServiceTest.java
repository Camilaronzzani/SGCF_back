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

        assertTrue(ex.getCause() instanceof ResponseStatusException);
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
}
