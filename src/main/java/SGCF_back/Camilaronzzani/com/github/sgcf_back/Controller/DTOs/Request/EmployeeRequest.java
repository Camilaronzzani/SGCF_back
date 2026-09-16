package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Employee;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CPF;
import java.time.LocalDate;
import java.util.List;


public record EmployeeRequest (

    @CPF(message = "the cnpj must be 11 characters")
    @Pattern(regexp = "^\\d{11}$")
     String cpf,

    @NotBlank
     String name,

    @NotBlank
    @NotEmpty
     List<Language> languagesSpoken ,

    @NotBlank
     LocalDate dayOfBirth
){
    public static Employee toEmployee(EmployeeRequest employeeRequest){
        Employee employee = new Employee();
        employee.setLanguagesSpoken(employeeRequest.languagesSpoken());
        employee.setCpf(employeeRequest.cpf());
        employee.setName(employeeRequest.name());
        employee.setDayOfBirth(employeeRequest.dayOfBirth());
        employee.setActive(true);
        return employee;
    }
}
