package Controller.DTOs;

import Entity.Enum.Language;
import jakarta.persistence.*;
import Entity.Employee;

import java.time.LocalDate;
import java.util.List;


public record EmployeDto( Long id , String cpf , String name , List<Language> languagesSpoken, LocalDate dayOfBirth) {
    public static EmployeDto toDto(Employee employee){
        return new EmployeDto(employee.getId(), employee.getCpf(), employee.getName(),employee.getLanguagesSpoken(), employee.getDayOfBirth());
    }
}
