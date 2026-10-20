package sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto;

import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Language;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Employee;

import java.time.LocalDate;
import java.util.List;


public record EmployeDto(Long id, String cpf, String name, List<Language> languagesSpoken,
                         LocalDate dayOfBirth, boolean active, long tourAmount, double cashReturn) {
    public static EmployeDto toDto(Employee employee){
        return new EmployeDto(employee.getId(), employee.getCpf(), employee.getName(), employee.getLanguagesSpoken(),
                employee.getDayOfBirth(), employee.isActive(), 0, 0);
    }
}
