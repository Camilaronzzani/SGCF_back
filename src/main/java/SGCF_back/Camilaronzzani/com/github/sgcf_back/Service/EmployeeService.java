package SGCF_back.Camilaronzzani.com.github.sgcf_back.Service;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.EmployeDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.EmployeeRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Employee;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Language;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.EmployeeRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.ReservationRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    public List<EmployeDto> findAll() {
        try {
            log.info("Employee list successfully");
            return employeeRepository.findAll()
                    .stream()
                    .map(EmployeDto :: toDto)
                    .toList();
        } catch (RuntimeException e) {
            log.error("Error in employeeService.findAll" , e );
            throw new RuntimeException(e);
        }

    }

    public EmployeDto findById(long id) {
        try {
            Employee employee = employeeRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "employee no find"));

            log.info("Employee {} found successfully" , employee.getName());
            return EmployeDto.toDto(employee);
        } catch (Exception e) {

            log.error("Error in employeeService.findById" , e );
            throw new RuntimeException(e);
        }
    }

    public String save(EmployeeRequest employeeRequest) {
        try {
            Employee employee = toEmployee(employeeRequest);
            employeeRepository.save(employee);

            log.info("Employee {} saved successfully" , employee.getName());
            return "Employee: " + employee.getName()+ " saved successfully ";

        } catch (Exception e) {
            log.error("Error in employeeService.findById" , e );
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

    public Employee changeDataByEmpoloyee(long id, EmployeeRequest newEmployee){

        Employee employeeOld = employeeRepository.findById(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        employeeOld.setCpf(newEmployee.getCpf());
        employeeOld.setName(newEmployee.getName());
        employeeOld.setDayOfBirth(newEmployee.getDayOfBirth());
        employeeOld.setLanguagesSpoken(newEmployee.getLanguagesSpoken());

        return employeeOld;

    }

    @Transactional
    public String update(EmployeeRequest employeeRequest, long id) {
        try {
            Employee employee = changeDataByEmpoloyee(id , employeeRequest);

            log.info("Employee {} updated successfully" , employee.getName());
            return "Employee: " + employee.getName() + " saved successfully ";

        } catch (Exception e) {
            log.error("Error in employeeService.update" , e );
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public String delete(long id) {
        try {
            Employee employee = employeeRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "employee no find"));
            employee.setActive(false);

            log.info("Employee {} deactivated successfully" , employee.getName());
            return "Employee: " + employee.getName() + " delete successfully ";

        } catch (Exception e) {
            log.error("Error in employeeService.delete" , e );
            throw new RuntimeException(e);
        }
    }

    // speak with the teacher to delete
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
            log.info("Employee list found successfully");
            return employeeRepository.findByActiveTrue()
                    .stream()
                    .map(EmployeDto :: toDto)
                    .toList();

        } catch (Exception e) {
            log.error("Error in employeeService.findAllActivate" , e );
            throw new RuntimeException(e);
        }
    }

    private EmployeDto toSummaryDto(Employee employee) {
        return new EmployeDto(employee.getId(), employee.getCpf(), employee.getName(), employee.getLanguagesSpoken(),
                employee.getDayOfBirth(), employee.isActive(), reservationRepository.countByEmployeeIdAndActiveTrue(employee.getId()),
                reservationRepository.sumValueByEmployeeAndStatus(employee.getId(), Status.Confirmed));
    }
}
