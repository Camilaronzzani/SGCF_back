package sgcf_back.camilaronzzani.com.github.sgcf_back.repositories;

import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long > {
    List<Employee> findByActiveTrue();
}
