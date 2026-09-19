package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import jakarta.validation.constraints.Positive;


public record PaymentRequest (

    @Positive
     Long customerId,

     Status status,

    @Positive
     double totalAccount
){
}
