package sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request;

import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Employee;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Language;
import jakarta.validation.constraints.NotBlank;
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

     List<Language> languagesSpoken ,

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
