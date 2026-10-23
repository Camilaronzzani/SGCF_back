package sgcf_back.camilaronzzani.com.github.sgcf_back.serviceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.BooleanRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.AuthenticateRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.ChangePasswordRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.UserRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Employee;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.User;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Permission;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.EmployeeRepository;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.UserRepository;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private UserService userService;

    // ===== findAll Tests =====

    @Test
    void findAllReturnsUsersFromRepository() {
        List<User> users = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.findAll();

        assertSame(users, result);
        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void findAllThrowsRuntimeExceptionWhenRepositoryFails() {
        when(userRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> userService.findAll());
        verify(userRepository).findAll();
    }

    // ===== findById Tests =====

    @Test
    void findByIdReturnsUserWhenItExists() {
        User user = new User();
        user.setId(1L);
        user.setUserName("testuser");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertSame(user, result);
        assertEquals("testuser", result.getUserName());
        verify(userRepository).findById(1L);
    }

    @Test
    void findByIdThrowsRuntimeExceptionWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.findById(1L));
        
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof ResponseStatusException);
        ResponseStatusException rse = (ResponseStatusException) exception.getCause();
        assertEquals(HttpStatus.NOT_FOUND, rse.getStatusCode());
        verify(userRepository).findById(1L);
    }

    // ===== save Tests =====

    @Test
    void savePersistsManagerUserWithoutEmployee() {
        UserRequest request = new UserRequest(
            "manager1",
            "password123",
            Permission.MANAGER,
            "manager@example.com",
            null
        );
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        userService.save(request);

        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(argThat(user ->
            user.getUserName().equals("manager1") &&
            user.getUserPassword().equals("encodedPassword") &&
            user.getPermission() == Permission.MANAGER &&
            user.getEmail().equals("manager@example.com") &&
            user.getEmployee() == null &&
            user.isActive()
        ));
    }

    @Test
    void savePersistsEmployeeUserWithValidEmployee() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("John Doe");

        UserRequest request = new UserRequest(
            "employee1",
            "password123",
            Permission.EMPLOYEE,
            "employee@example.com",
            1L
        );

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(userRepository.findByEmployeeId(1L)).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        userService.save(request);

        verify(employeeRepository).findById(1L);
        verify(userRepository).findByEmployeeId(1L);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(argThat(user ->
            user.getUserName().equals("employee1") &&
            user.getUserPassword().equals("encodedPassword") &&
            user.getPermission() == Permission.EMPLOYEE &&
            user.getEmployee() == employee
        ));
    }

    @Test
    void saveThrowsBadRequestWhenEmployeeUserHasNoEmployeeId() {
        UserRequest request = new UserRequest(
            "employee1",
            "password123",
            Permission.EMPLOYEE,
            "employee@example.com",
            null
        );

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> userService.save(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("employeeId is required for employee accounts", exception.getReason());
        verify(userRepository, never()).save(any());
    }

    @Test
    void saveThrowsNotFoundWhenEmployeeDoesNotExist() {
        UserRequest request = new UserRequest(
            "employee1",
            "password123",
            Permission.EMPLOYEE,
            "employee@example.com",
            999L
        );

        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> userService.save(request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("employee no find", exception.getReason());
        verify(employeeRepository).findById(999L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void saveThrowsConflictWhenEmployeeAlreadyHasAccount() {
        Employee employee = new Employee();
        employee.setId(1L);

        User existingUser = new User();
        existingUser.setId(10L);

        UserRequest request = new UserRequest(
            "employee1",
            "password123",
            Permission.EMPLOYEE,
            "employee@example.com",
            1L
        );

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(userRepository.findByEmployeeId(1L)).thenReturn(Optional.of(existingUser));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> userService.save(request));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("employee already has an account", exception.getReason());
        verify(employeeRepository).findById(1L);
        verify(userRepository).findByEmployeeId(1L);
        verify(userRepository, never()).save(any());
    }

    // ===== update Tests =====

    @Test
    void updateChangesUserFieldsAndReturnsUpdatedUser() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUserName("oldname");
        existingUser.setUserPassword("oldpassword");
        existingUser.setPermission(Permission.EMPLOYEE);
        existingUser.setEmail("old@example.com");

        UserRequest request = new UserRequest(
            "newname",
            "newpassword",
            Permission.MANAGER,
            "new@example.com",
            null
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");

        User result = userService.update(request, 1L);

        assertEquals("newname", result.getUserName());
        assertEquals("encodedNewPassword", result.getUserPassword());
        assertEquals(Permission.MANAGER, result.getPermission());
        assertEquals("new@example.com", result.getEmail());
        verify(userRepository).findById(1L);
        verify(passwordEncoder).encode("newpassword");
    }

    @Test
    void updateThrowsRuntimeExceptionWhenUserNotFound() {
        UserRequest request = new UserRequest(
            "newname",
            "newpassword",
            Permission.MANAGER,
            "new@example.com",
            null
        );

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.update(request, 999L));
        verify(userRepository).findById(999L);
        verify(passwordEncoder, never()).encode(anyString());
    }

    // ===== delete Tests =====

    @Test
    void deleteDeactivatesUserWhenItExists() {
        User user = new User();
        user.setId(1L);
        user.setUserName("testuser");
        user.setActive(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        assertFalse(user.isActive());
        verify(userRepository).findById(1L);
    }

    @Test
    void deleteThrowsRuntimeExceptionWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.delete(999L));
        verify(userRepository).findById(999L);
    }

    // ===== findAllActive Tests =====

    @Test
    void findAllActiveReturnsOnlyActiveUsers() {
        User user1 = new User();
        user1.setActive(true);
        User user2 = new User();
        user2.setActive(true);

        List<User> activeUsers = List.of(user1, user2);
        when(userRepository.findByActiveTrue()).thenReturn(activeUsers);

        List<User> result = userService.findAllActive();

        assertSame(activeUsers, result);
        assertEquals(2, result.size());
        verify(userRepository).findByActiveTrue();
    }

    @Test
    void findAllActiveThrowsRuntimeExceptionWhenRepositoryFails() {
        when(userRepository.findByActiveTrue()).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> userService.findAllActive());
        verify(userRepository).findByActiveTrue();
    }

    // ===== findByUserName Tests =====

    @Test
    void findByUserNameReturnsUserWhenItExists() {
        User user = new User();
        user.setId(1L);
        user.setUserName("testuser");

        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(user));

        User result = userService.findByUserName("testuser");

        assertSame(user, result);
        assertEquals("testuser", result.getUserName());
        verify(userRepository).findByUserName("testuser");
    }

    @Test
    void findByUserNameThrowsRuntimeExceptionWhenUserNotFound() {
        when(userRepository.findByUserName("nonexistent")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.findByUserName("nonexistent"));

        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof ResponseStatusException);
        verify(userRepository).findByUserName("nonexistent");
    }

    // ===== findByEmail Tests =====

    @Test
    void findByEmailReturnsUserWhenItExists() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User result = userService.findByEmail("test@example.com");

        assertSame(user, result);
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void findByEmailThrowsRuntimeExceptionWhenUserNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.findByEmail("nonexistent@example.com"));

        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof ResponseStatusException);
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    // ===== authenticate Tests =====

    @Test
    void authenticateReturnsTrueWhenPasswordMatches() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setUserPassword("encodedPassword");

        AuthenticateRequest request = new AuthenticateRequest("test@example.com", "password123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        Boolean result = userService.authenticate(request);

        assertTrue(result);
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123", "encodedPassword");
    }

    @Test
    void authenticateReturnsFalseWhenPasswordDoesNotMatch() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setUserPassword("encodedPassword");

        AuthenticateRequest request = new AuthenticateRequest("test@example.com", "wrongpassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        Boolean result = userService.authenticate(request);

        assertFalse(result);
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("wrongpassword", "encodedPassword");
    }

    @Test
    void authenticateThrowsRuntimeExceptionWhenUserNotFound() {
        AuthenticateRequest request = new AuthenticateRequest("nonexistent@example.com", "password123");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.authenticate(request));

        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof ResponseStatusException);
        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    // ===== changePassword Tests =====

    @Test
    void changePasswordUpdatesPasswordWhenOldPasswordDoesNotMatch() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setUserPassword("encodedOldPassword");

        ChangePasswordRequest request = new ChangePasswordRequest("test@example.com", "newPassword123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("encodedOldPassword", "newPassword123")).thenReturn(false);
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");

        BooleanRequest result = userService.changePassword(request);

        assertTrue(result.bool());
        assertEquals("Senha redefinida com sucesso", result.message());
        assertEquals("encodedNewPassword", user.getUserPassword());
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("encodedOldPassword", "newPassword123");
        verify(passwordEncoder).encode("newPassword123");
    }

    @Test
    void changePasswordFailsWhenOldPasswordMatchesNewPassword() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setUserPassword("encodedPassword");

        ChangePasswordRequest request = new ChangePasswordRequest("test@example.com", "samePassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("encodedPassword", "samePassword")).thenReturn(true);

        BooleanRequest result = userService.changePassword(request);

        assertFalse(result.bool());
        assertEquals("erro com a senha", result.message());
        assertEquals("encodedPassword", user.getUserPassword());
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("encodedPassword", "samePassword");
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void changePasswordThrowsRuntimeExceptionWhenUserNotFound() {
        ChangePasswordRequest request = new ChangePasswordRequest("nonexistent@example.com", "newPassword");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.changePassword(request));

        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof ResponseStatusException);
        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
    }
}
