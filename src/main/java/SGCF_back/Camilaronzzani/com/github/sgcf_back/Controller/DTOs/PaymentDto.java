package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Payment;

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
