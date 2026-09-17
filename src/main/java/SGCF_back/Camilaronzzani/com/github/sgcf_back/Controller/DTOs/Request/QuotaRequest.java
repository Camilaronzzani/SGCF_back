package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Employee;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Quota;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.PaymentService;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.QuotaService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;


public record QuotaRequest (

    LocalDate startDate,

     LocalDate endDate,

    @Positive
     double targetValue,

    @Positive
     Long employeeId
){
    @Autowired
    private static QuotaService quotaService;

    public static Quota toQuota(QuotaRequest quotaRequest) {
        Quota quota = new Quota();
        quota.setStartDate(quotaRequest.startDate());
        quota.setEndDate(quotaRequest.endDate());
        quota.setTargetValue(quotaRequest.targetValue());
        quota.setEmployee(quotaRequest.employeeId() == null
                ? null
                : quotaService.findEmployee(quotaRequest.employeeId()));
        quota.setActive(true);
        return quota;
    }

    public static Quota toEmployeeQuota(QuotaRequest quotaRequest) {
        Employee employee = quotaService.findEmployee(quotaRequest.employeeId());
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
