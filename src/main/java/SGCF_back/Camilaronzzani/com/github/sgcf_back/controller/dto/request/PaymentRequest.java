package sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request;

import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Status;
import jakarta.validation.constraints.Positive;


public record PaymentRequest (

    @Positive
     Long customerId,

     Status status,

    @Positive
     double totalAccount
){
}
