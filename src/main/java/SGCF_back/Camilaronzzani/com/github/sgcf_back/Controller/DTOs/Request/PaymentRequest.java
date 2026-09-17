package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Payment;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.PaymentService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;


public record PaymentRequest (

    @Positive
     Long customerId,

     Status status,

    @Positive
     double totalAccount
){
    @Autowired
    private static PaymentService paymentService;

    public static Payment toPayment(PaymentRequest paymentRequest) {
        Payment payment = new Payment();
        payment.setCustomer(paymentService.findCustomer(paymentRequest.customerId()));
        payment.setTotalAccount(paymentRequest.totalAccount());
        payment.setStatus(paymentRequest.status() == null
                ? Status.Pending
                : paymentRequest.status());
        payment.setActive(true);
        return payment;
    }
}
