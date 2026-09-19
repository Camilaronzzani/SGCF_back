package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Customer;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.CountryCustomer;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Language;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;
import java.util.List;

public record CustomerRequest (

    @CNPJ( message = "the cnpj must be 14 characters")
    @Pattern(regexp = "^\\d{14}$")
     String cnpj,

    @CPF(message = "the cnpj must be 11 characters")
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
