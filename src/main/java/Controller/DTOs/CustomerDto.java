package Controller.DTOs;

import Entity.Customer;
import Entity.Enum.CountryCustomer;
import Entity.Enum.Language;

import java.util.List;

public record CustomerDto(Long id  , String cnpj , String cpf , String name , List<Language> languageSpeak , CountryCustomer countryOfCostumer,String email){
    public static CustomerDto toDto(Customer customer){
        return new CustomerDto(customer.getId(), customer.getCnpj(), customer.getCpf(), customer.getName(),customer.getLanguageSpeak(),customer.getCountryCustomer() ,customer.getEmail());
    }
}
