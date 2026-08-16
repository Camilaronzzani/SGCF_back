package SGCF_back.Camilaronzzani.com.github.sgcf_back.Service;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.QuotaDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.QuotaRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Employee;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Permission;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Quota;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.EmployeeRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.QuotaRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.ReservationRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.context.event.EventListener;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class QuotaService {
    @Autowired
    private QuotaRepository quotaRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private HttpSession session;

    public List<QuotaDto> findAll() {
        try {
            List<Quota> quotaList = quotaRepository.findAll();
            List<QuotaDto> quotaDtos = new ArrayList<>();
            quotaList.forEach(quota -> {
                QuotaDto quotaDto = toDto(quota);
                quotaDtos.add(quotaDto);
            });
            return quotaDtos;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public QuotaDto findById(long id) {
        try {
            Optional<Quota> quota = Optional.of(quotaRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "quota no find")));
            return toDto(quota.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String save(QuotaRequest quotaRequest) {
        try {
            validateEmployeeQuota(quotaRequest);
            requireEmployeeAccess(quotaRequest.getEmployeeId());
            Quota quota = toEmployeeQuota(quotaRequest);
            quotaRepository.save(quota);
            return "Quota of target " + quota.getTargetValue() + " save successful ";
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Quota toQuota(QuotaRequest quotaRequest) {
        Quota quota = new Quota();
        quota.setStartDate(quotaRequest.getStartDate());
        quota.setEndDate(quotaRequest.getEndDate());
        quota.setTargetValue(quotaRequest.getTargetValue());
        quota.setEmployee(quotaRequest.getEmployeeId() == null
                ? null
                : findEmployee(quotaRequest.getEmployeeId()));
        quota.setActive(true);
        return quota;
    }

    public Quota toEmployeeQuota(QuotaRequest quotaRequest) {
        Employee employee = findEmployee(quotaRequest.getEmployeeId());
        LocalDate startDate = LocalDate.now();
        Quota quota = new Quota();
        quota.setStartDate(startDate);
        quota.setEndDate(startDate.plusDays(30));
        quota.setTargetValue(quotaRequest.getTargetValue());
        quota.setEmployee(employee);
        quota.setActive(true);
        return quota;
    }

    public void changeDataByQuota(Quota quotaOld, Quota newQuota) {
        quotaOld.setStartDate(newQuota.getStartDate());
        quotaOld.setEndDate(newQuota.getEndDate());
        quotaOld.setTargetValue(newQuota.getTargetValue());
        quotaOld.setEmployee(newQuota.getEmployee());
    }

    public String update(QuotaRequest quotaRequest, long id) {
        try {
            requireManager();
            Quota quotaOld = quotaRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "quota no find"));
            if (isCompanyQuota(quotaOld)) {
                validateCompanyTarget(quotaRequest.getTargetValue());
                quotaOld.setTargetValue(quotaRequest.getTargetValue());
            } else {
                validateEmployeeQuota(quotaRequest);
                validateQuotaDates(quotaRequest.getStartDate(), quotaRequest.getEndDate());
                changeDataByQuota(quotaOld, toQuota(quotaRequest));
            }
            quotaRepository.save(quotaOld);
            return "Quota: " + quotaOld.getId() + " update successful ";
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String delete(long id) {
        try {
            requireManager();
            Quota quota = quotaRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "quota no find"));
            if (isCompanyQuota(quota)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "company quota cannot be deleted");
            }
            quota.setActive(false);
            quotaRepository.save(quota);
            return "Quota: " + quota.getId() + " delete successful ";
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String applyPartialUpdate(long id, Map<String, Object> quota) {
        try {
            Quota quota1 = quotaRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "quota no find"));
            if (isCompanyQuota(quota1)) {
                requireManager();
                if (!quota.keySet().equals(Set.of("targetValue"))) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "only the company quota value can be changed");
                }
                validateCompanyTarget(quota.get("targetValue"));
            } else {
                requireManager();
                if (!quota.keySet().stream().allMatch(Set.of("startDate", "endDate", "targetValue", "employeeId")::contains)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unsupported quota field");
                }
                if (quota.containsKey("employeeId") && quota.get("employeeId") == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "employee quota requires an employee");
                }
            }
            quota.forEach((key, value) -> {
                switch (key) {
                    case "startDate" -> quota1.setStartDate(LocalDate.parse(value.toString()));
                    case "endDate" -> quota1.setEndDate(LocalDate.parse(value.toString()));
                    case "targetValue" -> quota1.setTargetValue(Double.parseDouble(value.toString()));
                    case "employeeId" -> quota1.setEmployee(value == null
                            ? null
                            : findEmployee(Long.parseLong(value.toString())));
                }
            });
            if (!isCompanyQuota(quota1)) {
                validateEmployeeTarget(quota1.getTargetValue());
                validateQuotaDates(quota1.getStartDate(), quota1.getEndDate());
            }
            quotaRepository.save(quota1);
            return "Quota: " + quota1.getId() + " update successful ";
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<QuotaDto> findAllActive() {
        try {
            List<Quota> quotaList = quotaRepository.findByActiveTrue();
            List<QuotaDto> quotaDtoList = new ArrayList<>();
            quotaList.forEach(quota -> {
                QuotaDto quotaDto = toDto(quota);
                quotaDtoList.add(quotaDto);
            });
            return quotaDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<QuotaDto> findByEmployee(long employeeId) {
        try {
            return quotaRepository.findByEmployeeId(employeeId)
                    .stream()
                    .map(this::toDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<QuotaDto> findAllCompany() {
        try {
            return quotaRepository.findByEmployeeIsNull()
                    .stream()
                    .map(this::toDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<QuotaDto> findAllProgress() {
        requireAuthenticated();
        Quota currentCompanyQuota = ensureCurrentCompanyQuota();
        Object sessionEmployeeId = session.getAttribute("employeeId");
        return quotaRepository.findByActiveTrue().stream()
            .filter(quota -> quota.getId().equals(currentCompanyQuota.getId())
                || (isManager() || isCurrentEmployeeQuota(quota, sessionEmployeeId)))
                .map(this::toDto)
                .toList();
    }

    @Scheduled(cron = "0 0 0 1 * *")
    public void startMonthlyCompanyQuota() {
        ensureCurrentCompanyQuota();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeCurrentCompanyQuota(ApplicationReadyEvent event) {
        ensureCurrentCompanyQuota();
    }

    private QuotaDto toDto(Quota quota) {
        if (quota.getStartDate() == null || quota.getEndDate() == null) {
            return QuotaDto.toDto(quota, 0);
        }
        LocalDate today = LocalDate.now();
        if (today.isBefore(quota.getStartDate())) {
            return QuotaDto.toDto(quota, 0);
        }
        LocalDate achievedUntil = quota.getEndDate().isBefore(today) ? quota.getEndDate() : today;
        double achievedValue = quota.getEmployee() == null
                ? reservationRepository.sumValueByStatusAndDateBetween(
                        Status.Confirmed, quota.getStartDate(), achievedUntil)
                : reservationRepository.sumValueByEmployeeAndStatusAndDateBetween(
                        quota.getEmployee().getId(), Status.Confirmed, quota.getStartDate(), achievedUntil);
        return QuotaDto.toDto(quota, achievedValue);
    }

    private Quota ensureCurrentCompanyQuota() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();
        Optional<Quota> currentQuota = quotaRepository.findByEmployeeIsNull().stream()
                .filter(quota -> quota.isActive()
                        && startDate.equals(quota.getStartDate())
                        && endDate.equals(quota.getEndDate()))
                .findFirst();
        if (currentQuota.isPresent()) {
            return currentQuota.get();
        }
        Quota quota = new Quota();
        quota.setStartDate(startDate);
        quota.setEndDate(endDate);
        quota.setTargetValue(5000);
        quota.setEmployee(null);
        quota.setActive(true);
        return quotaRepository.save(quota);
    }

    private boolean isCompanyQuota(Quota quota) {
        return quota.getEmployee() == null;
    }

    private void validateEmployeeQuota(QuotaRequest quotaRequest) {
        if (quotaRequest.getEmployeeId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "employeeId is required");
        }
        validateEmployeeTarget(quotaRequest.getTargetValue());
    }

    private void validateCompanyTarget(Object value) {
        if (value == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "targetValue is required");
        }
        validateCompanyTarget(parseTarget(value));
    }

    private void validateCompanyTarget(double targetValue) {
        if (!Double.isFinite(targetValue) || targetValue < 5000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "company quota must be at least 5000");
        }
    }

    private void validateEmployeeTarget(double targetValue) {
        if (!Double.isFinite(targetValue) || targetValue < 100 || targetValue > 10000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "employee quota must be between 100 and 10000");
        }
    }

    private void validateQuotaDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "quota dates are invalid");
        }
    }

    private double parseTarget(Object value) {
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "targetValue must be a number");
        }
    }

    private void requireManager() {
        if (!isManager()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "only managers can change the company quota");
        }
    }

    private void requireAuthenticated() {
        if (!(session.getAttribute("userId") instanceof Long)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "authentication is required");
        }
    }

    private boolean isManager() {
        return Permission.Manager.equals(session.getAttribute("permission"));
    }

    private void requireEmployeeAccess(Long employeeId) {
        requireAuthenticated();
        if (isManager()) {
            return;
        }
        Object sessionEmployeeId = session.getAttribute("employeeId");
        if (!(sessionEmployeeId instanceof Number number) || number.longValue() != employeeId.longValue()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "employees can only create their own quota");
        }
    }

    private boolean isCurrentEmployeeQuota(Quota quota, Object sessionEmployeeId) {
        return quota.getEmployee() != null
                && sessionEmployeeId instanceof Number number
                && quota.getEmployee().getId().equals(number.longValue());
    }

    private Employee findEmployee(Long employeeId) {
        if (employeeId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "employeeId is required");
        }
        return employeeRepository.findById(employeeId).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "employee no find"));
    }
}
