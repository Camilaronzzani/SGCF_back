package sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto;

import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Status;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Payment;

public record PaymentDto(Long id, Status status, CustomerDto customer, double totalAccount) {
    public static PaymentDto toDto(Payment payment) {
        return new PaymentDto(
                payment.getId(),
                payment.getStatus(),
                payment.getCustomer() == null ? null : CustomerDto.toDto(payment.getCustomer()),
                payment.getTotalAccount()
        );
    }
}
