package SGCF_back.Camilaronzzani.com.github.sgcf_back.Service;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.EmployeDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.EmployeeRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Employee;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Language;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.EmployeeRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.ReservationRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    public List<EmployeDto> findAll() {
        try {
            List<Employee> employees = employeeRepository.findAll();
            List<EmployeDto> employeDtos = new ArrayList<>();
            employees.forEach(employee -> {
                EmployeDto employeDto = toSummaryDto(employee);
                employeDtos.add(employeDto);
            });
            return employeDtos;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }

    public EmployeDto findById(long id) {
        try {
            Optional<Employee> employee = Optional.of(employeeRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "employee no find")));
            return EmployeDto.toDto(employee.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String save(EmployeeRequest employeeRequest) {
        try {
            Employee employee = toEmployee(employeeRequest);
            employeeRepository.save(employee);
            return "employee: " + employee.getName()+ " save successful ";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public Employee toEmployee(EmployeeRequest employeeRequest){
        Employee employee = new Employee();
        employee.setLanguagesSpoken(employeeRequest.getLanguagesSpoken());
        employee.setCpf(employeeRequest.getCpf());
        employee.setName(employeeRequest.getName());
        employee.setDayOfBirth(employeeRequest.getDayOfBirth());
        employee.setActive(true);
        return employee;
    }

    public void changeDataByEmpoloyee(Employee employeeOld, Employee newEmployee){

        employeeOld.setCpf(newEmployee.getCpf());
        employeeOld.setName(newEmployee.getName());
        employeeOld.setDayOfBirth(newEmployee.getDayOfBirth());
        employeeOld.setLanguagesSpoken(newEmployee.getLanguagesSpoken());

    }

    public String update(EmployeeRequest employeeRequest, long id) {
        try {
            Employee employeeNew = toEmployee(employeeRequest);
            Employee employeeOld = Optional.of(employeeRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "employee no find"))).get();
            changeDataByEmpoloyee(employeeOld , employeeNew);
            employeeRepository.save(employeeOld);
            return "employee: " + employeeOld.getName() + " save successful ";

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String delete(long id) {
        try {
            Employee employee = employeeRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "employee no find"));
            employee.setActive(false);
            employeeRepository.save(employee);
            return "employee: " + employee.getName() + " delete successful ";

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String applyPartialUpdate(long id, Map<String, Object> employee) {
        try {
            Employee employee1 = employeeRepository.findById(id).orElseThrow(()
                    ->  new ResponseStatusException(HttpStatus.NOT_FOUND, "employee no find"));
            employee.forEach((key , value) ->{
                switch (key){
                    case "cpf" -> employee1.setCpf((String) value) ;
                    case "name" -> employee1.setName((String) value);
                    case "languagesSpoken" -> employee1.setLanguagesSpoken((List<Language>) value);
                    case "dayOfBirth" -> employee1.setDayOfBirth((LocalDate) value);


                }
            });
            employeeRepository.save(employee1);
            return "employee: " + employee1.getName() + " delete successful ";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<EmployeDto> findAllActive() {
        try {
            List<Employee> employeeList = employeeRepository.findByActiveTrue();
            List<EmployeDto> customerDtoList = new ArrayList<>();
            employeeList.forEach(employee -> {
                EmployeDto employeDto = toSummaryDto(employee);
                customerDtoList.add(employeDto);
            });
            return customerDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private EmployeDto toSummaryDto(Employee employee) {
        return new EmployeDto(employee.getId(), employee.getCpf(), employee.getName(), employee.getLanguagesSpoken(),
                employee.getDayOfBirth(), employee.isActive(), reservationRepository.countByEmployeeIdAndActiveTrue(employee.getId()),
                reservationRepository.sumValueByEmployeeAndStatus(employee.getId(), Status.Confirmed));
    }
}
