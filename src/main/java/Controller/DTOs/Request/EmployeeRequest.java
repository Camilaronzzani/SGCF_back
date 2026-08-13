package Controller.DTOs.Request;

import Entity.Enum.Language;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EmployeeRequest {

    private String cpf;

    private String name;

    private List<Language> languagesSpoken = new ArrayList<>();

    private LocalDate dayOfBirth;
}
