package sgcf_back.camilaronzzani.com.github.sgcf_back;

import org.junit.jupiter.api.Test;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.AuthenticatedUserDto;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Employee;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.User;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuthenticatedUserDtoTest {

    @Test
    void mustMapUserWithEmployee() {
        Employee employee = new Employee();
        employee.setId(7L);

        User user = new User();
        user.setId(1L);
        user.setUserName("maria");
        user.setEmail("maria@teste.com");
        user.setPermission(Permission.values()[0]);
        user.setEmployee(employee);

        AuthenticatedUserDto dto = AuthenticatedUserDto.toDto(user);

        assertEquals(1L, dto.id());
        assertEquals("maria", dto.userName());
        assertEquals("maria@teste.com", dto.email());
        assertEquals(user.getPermission(), dto.permission());
        assertEquals(7L, dto.employeeId());
    }

    @Test
    void shouldReturnNullEmployeeIdWhenUserHasNoEmployee() {
        User user = new User();
        user.setId(1L);
        user.setUserName("admin");
        user.setEmployee(null);

        AuthenticatedUserDto dto = AuthenticatedUserDto.toDto(user);

        assertEquals(1L, dto.id());
        assertNull(dto.employeeId());
    }
}