package sgcf_back.camilaronzzani.com.github.sgcf_back.ServiceTest;

import jakarta.servlet.http.HttpSession;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.QuotaDto;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.QuotaRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Employee;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Quota;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Status;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.EmployeeRepository;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.QuotaRepository;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.ReservationRepository;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.QuotaService;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuotaServiceTest {
    @Mock
    private QuotaRepository quotaRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private HttpSession session;

    @InjectMocks
    private QuotaService quotaService;

    @Test
    void findAllReturnsQuotasFromRepository() {
        // Given
        List<Quota> quotas = List.of(new Quota());
        when(quotaRepository.findAll()).thenReturn(quotas);

        // When
        List<Quota> result = quotaService.findAll();

        // Then
        assertSame(quotas, result);
    }

    @Test
    void findByIdReturnsQuotaWhenItExists() {
        // Given
        Quota quota = new Quota();
        when(quotaRepository.findById(1L)).thenReturn(Optional.of(quota));

        // When
        Quota result = quotaService.findById(1L);

        // Then
        assertSame(quota, result);
    }

    @Test
    void findByIdWrapsNotFoundErrorWhenQuotaDoesNotExist() {
        // Given
        when(quotaRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> quotaService.findById(1L));

        // Then
        assertStatusCause(exception, HttpStatus.NOT_FOUND);
    }

    @Test
    void findAllActiveReturnsActiveQuotasFromRepository() {
        // Given
        List<Quota> quotas = List.of(new Quota());
        when(quotaRepository.findByActiveTrue()).thenReturn(quotas);

        // When
        List<Quota> result = quotaService.findAllActive();

        // Then
        assertSame(quotas, result);
    }

    @Test
    void findByEmployeeReturnsEmployeeQuotasFromRepository() {
        // Given
        List<Quota> quotas = List.of(new Quota());
        when(quotaRepository.findByEmployeeId(7L)).thenReturn(quotas);

        // When
        List<Quota> result = quotaService.findByEmployee(7L);

        // Then
        assertSame(quotas, result);
    }

    @Test
    void findAllCompanyReturnsUnassignedQuotasFromRepository() {
        // Given
        List<Quota> quotas = List.of(new Quota());
        when(quotaRepository.findByEmployeeIsNull()).thenReturn(quotas);

        // When
        List<Quota> result = quotaService.findAllCompany();

        // Then
        assertSame(quotas, result);
    }

    @Test
    void findEmployeeReturnsEmployeeWhenItExists() {
        // Given
        Employee employee = new Employee();
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));

        // When
        Employee result = quotaService.findEmployee(7L);

        // Then
        assertSame(employee, result);
    }

    @Test
    void findEmployeeRejectsMissingEmployeeId() {
        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> quotaService.findEmployee(null));

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void findEmployeeRejectsUnknownEmployee() {
        // Given
        when(employeeRepository.findById(7L)).thenReturn(Optional.empty());

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> quotaService.findEmployee(7L));

        // Then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void toQuotaMapsRequestWithEmployee() {
        // Given
        Employee employee = new Employee();
        LocalDate startDate = LocalDate.of(2026, 10, 1);
        LocalDate endDate = LocalDate.of(2026, 10, 31);
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));
        QuotaRequest request = new QuotaRequest(startDate, endDate, 1250.0, 7L);

        // When
        Quota quota = quotaService.toQuota(request);

        // Then
        assertEquals(startDate, quota.getStartDate());
        assertEquals(endDate, quota.getEndDate());
        assertEquals(1250.0, quota.getTargetValue());
        assertSame(employee, quota.getEmployee());
        assertTrue(quota.isActive());
    }

    @Test
    void toQuotaMapsCompanyRequestWithoutEmployee() {
        // Given
        QuotaRequest request = new QuotaRequest(null, null, 5000.0, null);

        // When
        Quota quota = quotaService.toQuota(request);

        // Then
        assertNull(quota.getEmployee());
        assertEquals(5000.0, quota.getTargetValue());
        assertTrue(quota.isActive());
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void toEmployeeQuotaCreatesThirtyDayActiveQuotaForEmployee() {
        // Given
        Employee employee = new Employee();
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));
        QuotaRequest request = new QuotaRequest(null, null, 1250.0, 7L);
        LocalDate today = LocalDate.now();

        // When
        Quota quota = quotaService.toEmployeeQuota(request);

        // Then
        assertEquals(today, quota.getStartDate());
        assertEquals(today.plusDays(30), quota.getEndDate());
        assertEquals(1250.0, quota.getTargetValue());
        assertSame(employee, quota.getEmployee());
        assertTrue(quota.isActive());
    }

    @Test
    void savePersistsTheMappedEmployeeQuota() {
        // Given
        Employee employee = new Employee();
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));
        QuotaRequest request = new QuotaRequest(null, null, 1250.0, 7L);

        // When
        quotaService.save(request);

        // Then
        verify(quotaRepository).save(argThat(quota ->
                quota.getEmployee() == employee
                        && quota.getTargetValue() == 1250.0
                        && quota.getStartDate().equals(LocalDate.now())
                        && quota.getEndDate().equals(LocalDate.now().plusDays(30))
                        && quota.isActive()));
    }

    @Test
    void changeDataByQuotaCopiesEditableFields() {
        // Given
        Quota oldQuota = new Quota();
        Employee employee = new Employee();
        Quota newQuota = new Quota(2L, LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 30), 2250.0, employee, true);

        // When
        quotaService.changeDataByQuota(oldQuota, newQuota);

        // Then
        assertEquals(newQuota.getStartDate(), oldQuota.getStartDate());
        assertEquals(newQuota.getEndDate(), oldQuota.getEndDate());
        assertEquals(newQuota.getTargetValue(), oldQuota.getTargetValue());
        assertSame(employee, oldQuota.getEmployee());
        assertNull(oldQuota.getId());
    }

    @Test
    void processQuotaUpdateChangesEmployeeQuotaFields() {
        // Given
        Employee employee = new Employee();
        Quota existing = new Quota(1L, LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30), 1000.0, employee, true);
        Employee replacement = new Employee();
        when(quotaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(employeeRepository.findById(8L)).thenReturn(Optional.of(replacement));
        QuotaRequest request = new QuotaRequest(LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31), 1500.0, 8L);

        // When
        Quota updated = quotaService.processQuotaUpdate(request, 1L);

        // Then
        assertSame(existing, updated);
        assertEquals(request.startDate(), updated.getStartDate());
        assertEquals(request.endDate(), updated.getEndDate());
        assertEquals(request.targetValue(), updated.getTargetValue());
        assertSame(replacement, updated.getEmployee());
    }

    @Test
    void processQuotaUpdateChangesOnlyTargetForCompanyQuota() {
        // Given
        LocalDate startDate = LocalDate.of(2026, 9, 1);
        LocalDate endDate = LocalDate.of(2026, 9, 30);
        Quota existing = new Quota(1L, startDate, endDate, 5000.0, null, true);
        when(quotaRepository.findById(1L)).thenReturn(Optional.of(existing));
        QuotaRequest request = new QuotaRequest(LocalDate.of(2026, 12, 1),
                LocalDate.of(2026, 12, 31), 7500.0, 7L);

        // When
        Quota updated = quotaService.processQuotaUpdate(request, 1L);

        // Then
        assertSame(existing, updated);
        assertEquals(7500.0, updated.getTargetValue());
        assertEquals(startDate, updated.getStartDate());
        assertEquals(endDate, updated.getEndDate());
        assertNull(updated.getEmployee());
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void updateWrapsMissingQuotaError() {
        // Given
        when(quotaRepository.findById(1L)).thenReturn(Optional.empty());
        QuotaRequest request = new QuotaRequest(null, null, 1000.0, 7L);

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> quotaService.update(request, 1L));

        // Then
        assertStatusCause(exception, HttpStatus.NOT_FOUND);
    }

    @Test
    void updateRejectsEmployeeQuotaWithoutEmployee() {
        // Given
        Employee employee = new Employee();
        Quota existing = new Quota(1L, LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30), 1000.0, employee, true);
        when(quotaRepository.findById(1L)).thenReturn(Optional.of(existing));
        QuotaRequest request = new QuotaRequest(LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31), 1000.0, null);

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> quotaService.update(request, 1L));

        // Then
        assertStatusCause(exception, HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateRejectsEmployeeQuotaBelowMinimumTarget() {
        // Given
        Employee employee = new Employee();
        Quota existing = new Quota(1L, LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30), 1000.0, employee, true);
        when(quotaRepository.findById(1L)).thenReturn(Optional.of(existing));
        QuotaRequest request = new QuotaRequest(LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31), 99.0, 7L);

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> quotaService.update(request, 1L));

        // Then
        assertStatusCause(exception, HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateRejectsEmployeeQuotaAboveMaximumTarget() {
        // Given
        Employee employee = new Employee();
        Quota existing = new Quota(1L, LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30), 1000.0, employee, true);
        when(quotaRepository.findById(1L)).thenReturn(Optional.of(existing));
        QuotaRequest request = new QuotaRequest(LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31), 10001.0, 7L);

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> quotaService.update(request, 1L));

        // Then
        assertStatusCause(exception, HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateRejectsEmployeeQuotaWithInvalidDates() {
        // Given
        Employee employee = new Employee();
        Quota existing = new Quota(1L, LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30), 1000.0, employee, true);
        when(quotaRepository.findById(1L)).thenReturn(Optional.of(existing));
        QuotaRequest request = new QuotaRequest(LocalDate.of(2026, 10, 31),
                LocalDate.of(2026, 10, 1), 1000.0, 7L);

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> quotaService.update(request, 1L));

        // Then
        assertStatusCause(exception, HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateRejectsCompanyQuotaBelowMinimumTarget() {
        // Given
        Quota existing = new Quota(1L, LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30), 5000.0, null, true);
        when(quotaRepository.findById(1L)).thenReturn(Optional.of(existing));
        QuotaRequest request = new QuotaRequest(null, null, 4999.0, null);

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> quotaService.update(request, 1L));

        // Then
        assertStatusCause(exception, HttpStatus.BAD_REQUEST);
    }

    @Test
    void deleteMarksEmployeeQuotaInactiveWithoutDeletingIt() {
        // Given
        Employee employee = new Employee();
        Quota quota = new Quota(1L, null, null, 1000.0, employee, true);
        when(quotaRepository.findById(1L)).thenReturn(Optional.of(quota));

        // When
        quotaService.delete(1L);

        // Then
        assertFalse(quota.isActive());
        verify(quotaRepository, never()).delete(any());
    }

    @Test
    void deleteRejectsCompanyQuota() {
        // Given
        Quota quota = new Quota(1L, null, null, 5000.0, null, true);
        when(quotaRepository.findById(1L)).thenReturn(Optional.of(quota));

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> quotaService.delete(1L));

        // Then
        assertStatusCause(exception, HttpStatus.FORBIDDEN);
        assertTrue(quota.isActive());
    }

    @Test
    void deleteWrapsMissingQuotaError() {
        // Given
        when(quotaRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> quotaService.delete(1L));

        // Then
        assertStatusCause(exception, HttpStatus.NOT_FOUND);
    }

    @Test
    void toDtoReturnsZeroAchievedValueWhenQuotaDatesAreMissing() {
        // Given
        Quota quota = new Quota(1L, null, LocalDate.now(), 1000.0, null, true);

        // When
        QuotaDto dto = quotaService.toDto(quota);

        // Then
        assertEquals(0.0, dto.achievedValue());
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void toDtoReturnsZeroAchievedValueForFutureQuota() {
        // Given
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Quota quota = new Quota(1L, tomorrow, tomorrow.plusDays(30),
                1000.0, null, true);

        // When
        QuotaDto dto = quotaService.toDto(quota);

        // Then
        assertEquals(0.0, dto.achievedValue());
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void toDtoUsesCompanyReservationTotalUntilToday() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now().plusDays(10);
        Quota quota = new Quota(1L, startDate, endDate, 5000.0, null, true);
        when(reservationRepository.sumValueByStatusAndDateBetween(
                Status.Confirmed, startDate, LocalDate.now())).thenReturn(2750.0);

        // When
        QuotaDto dto = quotaService.toDto(quota);

        // Then
        assertEquals(2750.0, dto.achievedValue());
        verify(reservationRepository).sumValueByStatusAndDateBetween(
                Status.Confirmed, startDate, LocalDate.now());
    }

    @Test
    void toDtoUsesEmployeeReservationTotalUntilQuotaEndDateWhenItHasExpired() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(20);
        LocalDate endDate = LocalDate.now().minusDays(5);
        Employee employee = new Employee();
        employee.setId(7L);
        Quota quota = new Quota(1L, startDate, endDate, 1000.0, employee, true);
        when(reservationRepository.sumValueByEmployeeAndStatusAndDateBetween(
                7L, Status.Confirmed, startDate, endDate)).thenReturn(850.0);

        // When
        QuotaDto dto = quotaService.toDto(quota);

        // Then
        assertEquals(850.0, dto.achievedValue());
        assertNotNull(dto.employee());
        verify(reservationRepository).sumValueByEmployeeAndStatusAndDateBetween(
                7L, Status.Confirmed, startDate, endDate);
    }

    private void assertStatusCause(RuntimeException exception, HttpStatus expectedStatus) {
        assertInstanceOf(ResponseStatusException.class, exception.getCause());
        assertEquals(expectedStatus,
                ((ResponseStatusException) exception.getCause()).getStatusCode());
    }
}
