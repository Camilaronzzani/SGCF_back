package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.CountryCustomer;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Language;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CustomerRequest {

    @CNPJ( message = "the cnpj must be 14 characters")
    @Pattern(regexp = "^\\d{14}$")
    private String cnpj;

    @CPF(message = "the cnpj must be 11 characters")
    @Pattern(regexp = "^\\d{11}$")
    private String cpf;

    @NotBlank
    private String name;

    private List<Language> languageSpeak = new ArrayList<>();

    @NotBlank
    private CountryCustomer countryCustomer;

    @NotBlank(message = "can't be blank")
    @Email
    private String email;

}
