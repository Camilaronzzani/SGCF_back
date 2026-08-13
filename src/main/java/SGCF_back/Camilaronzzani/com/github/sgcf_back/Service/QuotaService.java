package SGCF_back.Camilaronzzani.com.github.sgcf_back.Service;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.QuotaDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.QuotaRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Employee;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Quota;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.EmployeeRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.QuotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class QuotaService {
    @Autowired
    private QuotaRepository quotaRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    public List<QuotaDto> findAll() {
        try {
            List<Quota> quotaList = quotaRepository.findAll();
            List<QuotaDto> quotaDtos = new ArrayList<>();
            quotaList.forEach(quota -> {
                QuotaDto quotaDto = QuotaDto.toDto(quota);
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
            return QuotaDto.toDto(quota.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String save(QuotaRequest quotaRequest) {
        try {
            Quota quota = toQuota(quotaRequest);
            quotaRepository.save(quota);
            return "Quota of target " + quota.getTargetValue() + " save successful ";
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

    public void changeDataByQuota(Quota quotaOld, Quota newQuota) {
        quotaOld.setStartDate(newQuota.getStartDate());
        quotaOld.setEndDate(newQuota.getEndDate());
        quotaOld.setTargetValue(newQuota.getTargetValue());
        quotaOld.setEmployee(newQuota.getEmployee());
    }

    public String update(QuotaRequest quotaRequest, long id) {
        try {
            Quota quota = toQuota(quotaRequest);
            Quota quotaOld = Optional.of(quotaRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "quota no find"))).get();
            changeDataByQuota(quotaOld, quota);
            quotaRepository.save(quotaOld);
            return "Quota: " + quotaOld.getId() + " update successful ";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String delete(long id) {
        try {
            Quota quota = quotaRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "quota no find"));
            quota.setActive(false);
            quotaRepository.save(quota);
            return "Quota: " + quota.getId() + " delete successful ";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String applyPartialUpdate(long id, Map<String, Object> quota) {
        try {
            Quota quota1 = quotaRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "quota no find"));
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
            quotaRepository.save(quota1);
            return "Quota: " + quota1.getId() + " update successful ";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<QuotaDto> findAllActive() {
        try {
            List<Quota> quotaList = quotaRepository.findByActiveTrue();
            List<QuotaDto> quotaDtoList = new ArrayList<>();
            quotaList.forEach(quota -> {
                QuotaDto quotaDto = QuotaDto.toDto(quota);
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
                    .map(QuotaDto::toDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<QuotaDto> findAllCompany() {
        try {
            return quotaRepository.findByEmployeeIsNull()
                    .stream()
                    .map(QuotaDto::toDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Employee findEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "employee no find"));
    }
}
