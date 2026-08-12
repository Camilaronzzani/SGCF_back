package Controller.DTOs.Request;

import Entity.Enum.CountryCustomer;
import Entity.Enum.Language;
import jakarta.persistence.*;
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
