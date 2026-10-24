package sgcf_back.camilaronzzani.com.github.sgcf_back.entityTest;

import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeeTest {

    @Test
    void prePersistCreatesEmptyLanguagesList() {
        Employee employee = new Employee();

        ReflectionTestUtils.invokeMethod(employee, "prePersist");

        assertTrue(employee.getLanguagesSpoken().isEmpty());
    }
}