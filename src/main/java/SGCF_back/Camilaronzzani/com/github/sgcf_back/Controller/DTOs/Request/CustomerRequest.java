package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.CountryCustomer;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Language;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CustomerRequest {

    private String cnpj;

    private String cpf;

    private String name;

    private List<Language> languageSpeak = new ArrayList<>();

    private CountryCustomer countryCustomer;

    private String email;

}
