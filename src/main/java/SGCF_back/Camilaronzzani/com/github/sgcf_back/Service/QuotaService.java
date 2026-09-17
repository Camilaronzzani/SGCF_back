package SGCF_back.Camilaronzzani.com.github.sgcf_back.Service;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.QuotaDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.QuotaRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Employee;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Quota;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.EmployeeRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.QuotaRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.ReservationRepository;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
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

    public List<Quota> findAll() {
        try {
            List<Quota> quotaList = quotaRepository.findAll();


            log.info("Quota list found successfully");
            return quotaList;

        } catch (RuntimeException e) {
            log.error("Error in QuotaService.findAll ", e);
            throw new RuntimeException(e);
        }
    }

    public Quota findById(long id) {
        try {
            Quota quota = quotaRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "quota not found"));

            log.info("Quota {} found successfully " , id );
            return quota;

        } catch (Exception e) {
            log.error("Error in QuotaService.findById ", e);
            throw new RuntimeException(e);
        }
    }

    public void save(QuotaRequest quotaRequest) {
        try {
            Quota quota = toEmployeeQuota(quotaRequest);

            quotaRepository.save(quota);

            log.info("Quota {} saved successfully" , quota.getId());

        } catch (Exception e) {
            log.error("Error in QuotaService.save ", e);
            throw new RuntimeException(e);
        }
    }

    public void changeDataByQuota(Quota quotaOld, Quota newQuota) {
        quotaOld.setStartDate(newQuota.getStartDate());
        quotaOld.setEndDate(newQuota.getEndDate());
        quotaOld.setTargetValue(newQuota.getTargetValue());
        quotaOld.setEmployee(newQuota.getEmployee());
    }

    public Quota processQuotaUpdate(QuotaRequest quotaRequest, long id){
        Quota quotaOld = quotaRepository.findById(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quota not found"));

        if (isCompanyQuota(quotaOld)) {
            validateCompanyTarget(quotaRequest.targetValue());
            quotaOld.setTargetValue(quotaRequest.targetValue());

        } else {

            validateEmployeeQuota(quotaRequest);
            validateQuotaDates(quotaRequest.startDate(), quotaRequest.endDate());
            changeDataByQuota(quotaOld, toQuota(quotaRequest));
        }
        return quotaOld ;
    }
    @Transactional
    public Quota update(QuotaRequest quotaRequest, long id) {
        try {
            Quota quota = processQuotaUpdate(quotaRequest,id);

            log.info("Quota updated successfully");
            return quota;

        } catch (Exception e) {
            log.error("Error in QuotaService.update ", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void delete(long id) {
        try {
            Quota quota = quotaRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "quota not found"));

            if (isCompanyQuota(quota)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "company quota cannot be deleted");
            }

            quota.setActive(false);

            log.info("Quota {} deleted successfully", id);

        } catch (Exception e) {
            log.error("Error in QuotaService.delete ", e);
            throw new RuntimeException(e);
        }
    }

    public List<Quota> findAllActive() {
        try {
            List<Quota> quotaList = quotaRepository.findByActiveTrue();

            log.info("Quota list active found successfully");
            return quotaList;
        } catch (Exception e) {
            log.error("Error in QuotaService.findAllActive ", e);
            throw new RuntimeException(e);
        }
    }

    public List<Quota> findByEmployee(long employeeId) {
        try {
            log.info("Fetches employee ");
            return quotaRepository.findByEmployeeId(employeeId);
        } catch (Exception e) {
            log.error("Error in QuotaService.findByEmployee ", e);
            throw new RuntimeException(e);
        }
    }

    public List<Quota> findAllCompany() {
        try {
            log.info("Fetches company ");
            return quotaRepository.findByEmployeeIsNull();
        } catch (Exception e) {
            log.error("Error in QuotaService.findAllCompany ", e);
            throw new RuntimeException(e);
        }
    }


    public QuotaDto toDto(Quota quota) {
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

    private boolean isCompanyQuota(Quota quota) {
        return quota.getEmployee() == null;
    }

    private void validateEmployeeQuota(QuotaRequest quotaRequest) {
        if (quotaRequest.employeeId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "employeeId is required");
        }
        validateEmployeeTarget(quotaRequest.targetValue());
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


    public Employee findEmployee(Long employeeId) {
        if (employeeId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "employeeId is required");
        }
        return employeeRepository.findById(employeeId).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "employee no find"));
    }

    public Quota toQuota(QuotaRequest quotaRequest) {
        Quota quota = new Quota();
        quota.setStartDate(quotaRequest.startDate());
        quota.setEndDate(quotaRequest.endDate());
        quota.setTargetValue(quotaRequest.targetValue());
        quota.setEmployee(quotaRequest.employeeId() == null  ? null
                : findEmployee(quotaRequest.employeeId()));
        quota.setActive(true);
        return quota;
    }

    public Quota toEmployeeQuota(QuotaRequest quotaRequest) {
        Employee employee = findEmployee(quotaRequest.employeeId());
        LocalDate startDate = LocalDate.now();

        Quota quota = new Quota();
        quota.setStartDate(startDate);
        quota.setEndDate(startDate.plusDays(30));
        quota.setTargetValue(quotaRequest.targetValue());
        quota.setEmployee(employee);
        quota.setActive(true);
        return quota;
    }
}
