package sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto;

import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Customer;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.CountryCustomer;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Language;

import java.util.List;

public record CustomerDto(Long id  , String cnpj , String cpf , String name , List<Language> languageSpeak , CountryCustomer countryOfCostumer,String email){
    public static CustomerDto toDto(Customer customer){
        return new CustomerDto(customer.getId(), customer.getCnpj(), customer.getCpf(), customer.getName(),customer.getLanguageSpeak(),customer.getCountryCustomer() ,customer.getEmail());
    }
}
