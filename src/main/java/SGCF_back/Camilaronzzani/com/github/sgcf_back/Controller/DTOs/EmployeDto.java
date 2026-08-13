package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Language;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Employee;

import java.time.LocalDate;
import java.util.List;


public record EmployeDto( Long id , String cpf , String name , List<Language> languagesSpoken, LocalDate dayOfBirth) {
    public static EmployeDto toDto(Employee employee){
        return new EmployeDto(employee.getId(), employee.getCpf(), employee.getName(),employee.getLanguagesSpoken(), employee.getDayOfBirth());
    }
}
