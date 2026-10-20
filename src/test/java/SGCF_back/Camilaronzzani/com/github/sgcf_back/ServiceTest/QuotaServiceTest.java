package sgcf_back.camilaronzzani.com.github.sgcf_back.ServiceTest;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.QuotaDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.QuotaRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Employee;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Quota;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.EmployeeRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.QuotaRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.ReservationRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.QuotaService;
import jakarta.servlet.http.HttpSession;
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
        List<Quota> quotas = List.of(new Quota());
        when(quotaRepository.findAll()).thenReturn(quotas);

        assertSame(quotas, quotaService.findAll());
    }

    @Test
    void findByIdReturnsQuotaWhenItExists() {
        Quota quota = new Quota();
        when(quotaRepository.findById(1L)).thenReturn(Optional.of(quota));

        assertSame(quota, quotaService.findById(1L));
    }

    @Test
    void findByIdWrapsNotFoundErrorWhenQuotaDoesNotExist() {
        when(quotaRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> quotaService.findById(1L));

        assertStatusCause(exception, HttpStatus.NOT_FOUND);
    }

    @Test
    void findAllActiveReturnsActiveQuotasFromRepository() {
        List<Quota> quotas = List.of(new Quota());
        when(quotaRepository.findByActiveTrue()).thenReturn(quotas);

        assertSame(quotas, quotaService.findAllActive());
    }

    @Test
    void findByEmployeeReturnsEmployeeQuotasFromRepository() {
        List<Quota> quotas = List.of(new Quota());
        when(quotaRepository.findByEmployeeId(7L)).thenReturn(quotas);

        assertSame(quotas, quotaService.findByEmployee(7L));
    }

    @Test
    void findAllCompanyReturnsUnassignedQuotasFromRepository() {
        List<Quota> quotas = List.of(new Quota());
        when(quotaRepository.findByEmployeeIsNull()).thenReturn(quotas);

        assertSame(quotas, quotaService.findAllCompany());
    }

    @Test
    void findEmployeeReturnsEmployeeWhenItExists() {
        Employee employee = new Employee();
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));

        assertSame(employee, quotaService.findEmployee(7L));
    }

    @Test
    void findEmployeeRejectsMissingEmployeeId() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> quotaService.findEmployee(null));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void findEmployeeRejectsUnknownEmployee() {
        when(employeeRepository.findById(7L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> quotaService.findEmployee(7L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void toQuotaMapsRequestWithEmployee() {
        Employee employee = new Employee();
        LocalDate startDate = LocalDate.of(2026, 10, 1);
        LocalDate endDate = LocalDate.of(2026, 10, 31);
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));
        QuotaRequest request = new QuotaRequest(startDate, endDate, 1250.0, 7L);

        Quota quota = quotaService.toQuota(request);

        assertEquals(startDate, quota.getStartDate());
        assertEquals(endDate, quota.getEndDate());
        assertEquals(1250.0, quota.getTargetValue());
        assertSame(employee, quota.getEmployee());
        assertTrue(quota.isActive());
    }

    @Test
    void toQuotaMapsCompanyRequestWithoutEmployee() {
        QuotaRequest request = new QuotaRequest(null, null, 5000.0, null);

        Quota quota = quotaService.toQuota(request);

        assertNull(quota.getEmployee());
        assertEquals(5000.0, quota.getTargetValue());
        assertTrue(quota.isActive());
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void toEmployeeQuotaCreatesThirtyDayActiveQuotaForEmployee() {
        Employee employee = new Employee();
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));
        QuotaRequest request = new QuotaRequest(null, null, 1250.0, 7L);
        LocalDate today = LocalDate.now();

        Quota quota = quotaService.toEmployeeQuota(request);

        assertEquals(today, quota.getStartDate());
        assertEquals(today.plusDays(30), quota.getEndDate());
        assertEquals(1250.0, quota.getTargetValue());
        assertSame(employee, quota.getEmployee());
        assertTrue(quota.isActive());
    }

    @Test
    void savePersistsTheMappedEmployeeQuota() {
        Employee employee = new Employee();
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));
        QuotaRequest request = new QuotaRequest(null, null, 1250.0, 7L);

        quotaService.save(request);

        verify(quotaRepository).save(argThat(quota ->
                quota.getEmployee() == employee
                        && quota.getTargetValue() == 1250.0
                        && quota.getStartDate().equals(LocalDate.now())
                        && quota.getEndDate().equals(LocalDate.now().plusDays(30))
                        && quota.isActive()));
    }

    @Test
    void changeDataByQuotaCopiesEditableFields() {
        Quota oldQuota = new Quota();
        Employee employee = new Employee();
        Quota newQuota = new Quota(2L, LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 30), 2250.0, employee, true);

        quotaService.changeDataByQuota(oldQuota, newQuota);

        assertEquals(newQuota.getStartDate(), oldQuota.getStartDate());
        assertEquals(newQuota.getEndDate(), oldQuota.getEndDate());
        assertEquals(newQuota.getTargetValue(), oldQuota.getTargetValue());
        assertSame(employee, oldQuota.getEmployee());
        assertNull(oldQuota.getId());
    }

    @Test
    void processQuotaUpdateChangesEmployeeQuotaFields() {
        Employee employee = new Employee();
        Quota existing = new Quota(1L, LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30), 1000.0, employee, true);
        Employee replacement = new Employee();
        when(quotaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(employeeRepository.findById(8L)).thenReturn(Optional.of(replacement));
        QuotaRequest request = new QuotaRequest(LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31), 1500.0, 8L);

        Quota updated = quotaService.processQuotaUpdate(request, 1L);

        assertSame(existing, updated);
        assertEquals(request.startDate(), updated.getStartDate());
        assertEquals(request.endDate(), updated.getEndDate());
        assertEquals(request.targetValue(), updated.getTargetValue());
        assertSame(replacement, updated.getEmployee());
    }

    @Test
    void processQuotaUpdateChangesOnlyTargetForCompanyQuota() {
        LocalDate startDate = LocalDate.of(2026, 9, 1);
        LocalDate endDate = LocalDate.of(2026, 9, 30);
        Quota existing = new Quota(1L, startDate, endDate, 5000.0, null, true);
        when(quotaRepository.findById(1L)).thenReturn(Optional.of(existing));
        QuotaRequest request = new QuotaRequest(LocalDate.of(2026, 12, 1),
                LocalDate.of(2026, 12, 31), 7500.0, 7L);

        Quota updated = quotaService.processQuotaUpdate(request, 1L);

        assertSame(existing, updated);
        assertEquals(7500.0, updated.getTargetValue());
        assertEquals(startDate, updated.getStartDate());
        assertEquals(endDate, updated.getEndDate());
        assertNull(updated.getEmployee());
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void updateWrapsMissingQuotaError() {
        when(quotaRepository.findById(1L)).thenReturn(Optional.empty());
        QuotaRequest request = new QuotaRequest(null, null, 1000.0, 7L);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> quotaService.update(request, 1L));

        assertStatusCause(exception, HttpStatus.NOT_FOUND);
    }

    @Test
    void updateRejectsEmployeeQuotaWithoutEmployee() {
        Employee employee = new Employee();
        Quota existing = new Quota(1L, LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30), 1000.0, employee, true);
        when(quotaRepository.findById(1L)).thenReturn(Optional.of(existing));
        QuotaRequest request = new QuotaRequest(LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31), 1000.0, null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> quotaService.update(request, 1L));

        assertStatusCause(exception, HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateRejectsEmployeeQuotaBelowMinimumTarget() {
        Employee employee = new Employee();
        Quota existing = new Quota(1L, LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30), 1000.0, employee, true);
        when(quotaRepository.findById(1L)).thenReturn(Optional.of(existing));
        QuotaRequest request = new QuotaRequest(LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31), 99.0, 7L);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> quotaService.update(request, 1L));

        assertStatusCause(exception, HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateRejectsEmployeeQuotaAboveMaximumTarget() {
        Employee employee = new Employee();
        Quota existing = new Quota(1L, LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30), 1000.0, employee, true);
        when(quotaRepository.findById(1L)).thenReturn(Optional.of(existing));
        QuotaRequest request = new QuotaRequest(LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31), 10001.0, 7L);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> quotaService.update(request, 1L));

        assertStatusCause(exception, HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateRejectsEmployeeQuotaWithInvalidDates() {
        Employee employee = new Employee();
        Quota existing = new Quota(1L, LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30), 1000.0, employee, true);
        when(quotaRepository.findById(1L)).thenReturn(Optional.of(existing));
        QuotaRequest request = new QuotaRequest(LocalDate.of(2026, 10, 31),
                LocalDate.of(2026, 10, 1), 1000.0, 7L);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> quotaService.update(request, 1L));

        assertStatusCause(exception, HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateRejectsCompanyQuotaBelowMinimumTarget() {
        Quota existing = new Quota(1L, LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30), 5000.0, null, true);
        when(quotaRepository.findById(1L)).thenReturn(Optional.of(existing));
        QuotaRequest request = new QuotaRequest(null, null, 4999.0, null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> quotaService.update(request, 1L));

        assertStatusCause(exception, HttpStatus.BAD_REQUEST);
    }

    @Test
    void deleteMarksEmployeeQuotaInactiveWithoutDeletingIt() {
        Employee employee = new Employee();
        Quota quota = new Quota(1L, null, null, 1000.0, employee, true);
        when(quotaRepository.findById(1L)).thenReturn(Optional.of(quota));

        quotaService.delete(1L);

        assertFalse(quota.isActive());
        verify(quotaRepository, never()).delete(any());
    }

    @Test
    void deleteRejectsCompanyQuota() {
        Quota quota = new Quota(1L, null, null, 5000.0, null, true);
        when(quotaRepository.findById(1L)).thenReturn(Optional.of(quota));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> quotaService.delete(1L));

        assertStatusCause(exception, HttpStatus.FORBIDDEN);
        assertTrue(quota.isActive());
    }

    @Test
    void deleteWrapsMissingQuotaError() {
        when(quotaRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> quotaService.delete(1L));

        assertStatusCause(exception, HttpStatus.NOT_FOUND);
    }

    @Test
    void toDtoReturnsZeroAchievedValueWhenQuotaDatesAreMissing() {
        Quota quota = new Quota(1L, null, LocalDate.now(), 1000.0, null, true);

        QuotaDto dto = quotaService.toDto(quota);

        assertEquals(0.0, dto.achievedValue());
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void toDtoReturnsZeroAchievedValueForFutureQuota() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Quota quota = new Quota(1L, tomorrow, tomorrow.plusDays(30),
                1000.0, null, true);

        QuotaDto dto = quotaService.toDto(quota);

        assertEquals(0.0, dto.achievedValue());
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void toDtoUsesCompanyReservationTotalUntilToday() {
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now().plusDays(10);
        Quota quota = new Quota(1L, startDate, endDate, 5000.0, null, true);
        when(reservationRepository.sumValueByStatusAndDateBetween(
                Status.Confirmed, startDate, LocalDate.now())).thenReturn(2750.0);

        QuotaDto dto = quotaService.toDto(quota);

        assertEquals(2750.0, dto.achievedValue());
        verify(reservationRepository).sumValueByStatusAndDateBetween(
                Status.Confirmed, startDate, LocalDate.now());
    }

    @Test
    void toDtoUsesEmployeeReservationTotalUntilQuotaEndDateWhenItHasExpired() {
        LocalDate startDate = LocalDate.now().minusDays(20);
        LocalDate endDate = LocalDate.now().minusDays(5);
        Employee employee = new Employee();
        employee.setId(7L);
        Quota quota = new Quota(1L, startDate, endDate, 1000.0, employee, true);
        when(reservationRepository.sumValueByEmployeeAndStatusAndDateBetween(
                7L, Status.Confirmed, startDate, endDate)).thenReturn(850.0);

        QuotaDto dto = quotaService.toDto(quota);

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
