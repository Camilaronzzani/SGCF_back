package sgcf_back.camilaronzzani.com.github.sgcf_back.service;

import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.EmployeDto;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.EmployeeRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Employee;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.EmployeeRepository;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.ReservationRepository;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.EmployeeRequest.toEmployee;

@Slf4j
@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    public List<Employee> findAll() {
        try {
            log.info("Fetches employee list ");
            return employeeRepository.findAll();
        } catch (RuntimeException e) {
            log.error("Error in employeeService.findAll" , e );
            throw new RuntimeException(e);
        }

    }

    public Employee findById(long id) {
        try {
            Employee employee = employeeRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "employee no find"));

            log.info("EMPLOYEE {} found successfully" , employee.getName());
            return employee;
        } catch (Exception e) {

            log.error("Error in employeeService.findById" , e );
            throw new RuntimeException(e);
        }
    }

    public void save(EmployeeRequest employeeRequest) {
        try {
            Employee employee = toEmployee(employeeRequest);
            employeeRepository.save(employee);

            log.info("EMPLOYEE {} saved successfully" , employee.getName());

        } catch (Exception e) {
            log.error("Error in employeeService.findById" , e );
            throw new RuntimeException(e);
        }
    }


    public Employee changeDataByEmpoloyee(long id, EmployeeRequest newEmployee){

        Employee employeeOld = employeeRepository.findById(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "EMPLOYEE not found"));

        employeeOld.setCpf(newEmployee.cpf());
        employeeOld.setName(newEmployee.name());
        employeeOld.setDayOfBirth(newEmployee.dayOfBirth());
        employeeOld.setLanguagesSpoken(newEmployee.languagesSpoken());

        return employeeOld;

    }

    @Transactional
    public Employee update(EmployeeRequest employeeRequest, long id) {
        try {
            Employee employee = changeDataByEmpoloyee(id , employeeRequest);

            log.info("EMPLOYEE {} updated successfully" , employee.getName());
            return employee;

        } catch (Exception e) {
            log.error("Error in employeeService.update" , e );
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void delete(long id) {
        try {
            Employee employee = employeeRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "employee no find"));
            employee.setActive(false);

            log.info("EMPLOYEE {} deactivated successfully" , employee.getName());

        } catch (Exception e) {
            log.error("Error in employeeService.delete" , e );
            throw new RuntimeException(e);
        }
    }

    public List<Employee> findAllActive() {
        try {
            log.info("EMPLOYEE list found successfully");
            return employeeRepository.findByActiveTrue();

        } catch (Exception e) {
            log.error("Error in employeeService.findAllActivate" , e );
            throw new RuntimeException(e);
        }
    }

    public EmployeDto toSummaryDto(Employee employee) {
        return new EmployeDto(employee.getId(), employee.getCpf(), employee.getName(), employee.getLanguagesSpoken(),
                employee.getDayOfBirth(), employee.isActive(), reservationRepository.countByEmployeeIdAndActiveTrue(employee.getId()),
                reservationRepository.sumValueByEmployeeAndStatus(employee.getId(), Status.CONFIRMED));
    }
}
