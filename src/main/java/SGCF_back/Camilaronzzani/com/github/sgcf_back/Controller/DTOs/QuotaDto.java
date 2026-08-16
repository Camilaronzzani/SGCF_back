package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Quota;

import java.time.LocalDate;

public record QuotaDto(Long id, LocalDate startDate, LocalDate endDate, double targetValue, double achievedValue, EmployeDto employee) {
    public static QuotaDto toDto(Quota quota, double achievedValue) {
        return new QuotaDto(
                quota.getId(),
                quota.getStartDate(),
                quota.getEndDate(),
                quota.getTargetValue(),
                achievedValue,
                quota.getEmployee() == null ? null : EmployeDto.toDto(quota.getEmployee())
        );
    }
}
