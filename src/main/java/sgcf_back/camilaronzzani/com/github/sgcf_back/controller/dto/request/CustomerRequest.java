package sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request;

import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Customer;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.CountryCustomer;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Language;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;
import java.util.List;

public record CustomerRequest (

    @CNPJ( message = "o CNPJ deve ter 14 dígitos válidos")
    @Pattern(regexp = "^\\d{14}$")
     String cnpj,

    @CPF(message = "o CPF deve ter 11 dígitos válidos")
    @Pattern(regexp = "^\\d{11}$")
     String cpf,

    @NotBlank
     String name,

    List<Language> languageSpeak ,


     CountryCustomer countryCustomer ,

    @NotBlank(message = "can't be blank")
    @Email
     String email

){
    public static Customer toCustomer(CustomerRequest customerRequest){
        Customer customer = new Customer();
        customer.setCountryCustomer(customerRequest.countryCustomer());
        customer.setCnpj(customerRequest.cnpj());
        customer.setCpf(customerRequest.cpf());
        customer.setName(customerRequest.name());
        customer.setLanguageSpeak(customerRequest.languageSpeak());
        customer.setEmail(customerRequest.email());
        customer.setActive(true);
        return customer;
    }
}
