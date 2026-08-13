package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Quota;

import java.time.LocalDate;

public record QuotaDto(Long id, LocalDate startDate, LocalDate endDate, double targetValue, EmployeDto employee) {
    public static QuotaDto toDto(Quota quota) {
        return new QuotaDto(
                quota.getId(),
                quota.getStartDate(),
                quota.getEndDate(),
                quota.getTargetValue(),
                quota.getEmployee() == null ? null : EmployeDto.toDto(quota.getEmployee())
        );
    }
}
