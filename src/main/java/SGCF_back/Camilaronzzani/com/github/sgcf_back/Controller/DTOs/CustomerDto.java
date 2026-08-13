package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Customer;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.CountryCustomer;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Language;

import java.util.List;

public record CustomerDto(Long id  , String cnpj , String cpf , String name , List<Language> languageSpeak , CountryCustomer countryOfCostumer,String email){
    public static CustomerDto toDto(Customer customer){
        return new CustomerDto(customer.getId(), customer.getCnpj(), customer.getCpf(), customer.getName(),customer.getLanguageSpeak(),customer.getCountryCustomer() ,customer.getEmail());
    }
}
