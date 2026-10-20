package sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto;

import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Quota;

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
