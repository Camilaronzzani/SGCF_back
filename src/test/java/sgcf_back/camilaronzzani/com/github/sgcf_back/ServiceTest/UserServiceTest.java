package sgcf_back.camilaronzzani.com.github.sgcf_back.ServiceTest;

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
        // Given
        List<User> users = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> result = userService.findAll();

        // Then
        assertSame(users, result);
        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void findAllThrowsRuntimeExceptionWhenRepositoryFails() {
        // Given
        when(userRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When
        assertThrows(RuntimeException.class, () -> userService.findAll());

        // Then
        verify(userRepository).findAll();
    }

    // ===== findById Tests =====

    @Test
    void findByIdReturnsUserWhenItExists() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUserName("testuser");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        User result = userService.findById(1L);

        // Then
        assertSame(user, result);
        assertEquals("testuser", result.getUserName());
        verify(userRepository).findById(1L);
    }

    @Test
    void findByIdThrowsRuntimeExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.findById(1L));

        // Then
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof ResponseStatusException);
        ResponseStatusException rse = (ResponseStatusException) exception.getCause();
        assertEquals(HttpStatus.NOT_FOUND, rse.getStatusCode());
        verify(userRepository).findById(1L);
    }

    // ===== save Tests =====

    @Test
    void savePersistsManagerUserWithoutEmployee() {
        // Given
        UserRequest request = new UserRequest(
            "manager1",
            "password123",
            Permission.Manager,
            "manager@example.com",
            null
        );
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        // When
        userService.save(request);

        // Then
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(argThat(user ->
            user.getUserName().equals("manager1") &&
            user.getUserPassword().equals("encodedPassword") &&
            user.getPermission() == Permission.Manager &&
            user.getEmail().equals("manager@example.com") &&
            user.getEmployee() == null &&
            user.isActive()
        ));
    }

    @Test
    void savePersistsEmployeeUserWithValidEmployee() {
        // Given
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("John Doe");

        UserRequest request = new UserRequest(
            "employee1",
            "password123",
            Permission.Employee,
            "employee@example.com",
            1L
        );

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(userRepository.findByEmployeeId(1L)).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        // When
        userService.save(request);

        // Then
        verify(employeeRepository).findById(1L);
        verify(userRepository).findByEmployeeId(1L);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(argThat(user ->
            user.getUserName().equals("employee1") &&
            user.getUserPassword().equals("encodedPassword") &&
            user.getPermission() == Permission.Employee &&
            user.getEmployee() == employee
        ));
    }

    @Test
    void saveThrowsBadRequestWhenEmployeeUserHasNoEmployeeId() {
        // Given
        UserRequest request = new UserRequest(
            "employee1",
            "password123",
            Permission.Employee,
            "employee@example.com",
            null
        );

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> userService.save(request));

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("employeeId is required for employee accounts", exception.getReason());
        verify(userRepository, never()).save(any());
    }

    @Test
    void saveThrowsNotFoundWhenEmployeeDoesNotExist() {
        // Given
        UserRequest request = new UserRequest(
            "employee1",
            "password123",
            Permission.Employee,
            "employee@example.com",
            999L
        );

        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> userService.save(request));

        // Then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("employee no find", exception.getReason());
        verify(employeeRepository).findById(999L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void saveThrowsConflictWhenEmployeeAlreadyHasAccount() {
        // Given
        Employee employee = new Employee();
        employee.setId(1L);

        User existingUser = new User();
        existingUser.setId(10L);

        UserRequest request = new UserRequest(
            "employee1",
            "password123",
            Permission.Employee,
            "employee@example.com",
            1L
        );

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(userRepository.findByEmployeeId(1L)).thenReturn(Optional.of(existingUser));

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> userService.save(request));

        // Then
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("employee already has an account", exception.getReason());
        verify(employeeRepository).findById(1L);
        verify(userRepository).findByEmployeeId(1L);
        verify(userRepository, never()).save(any());
    }

    // ===== update Tests =====

    @Test
    void updateChangesUserFieldsAndReturnsUpdatedUser() {
        // Given
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUserName("oldname");
        existingUser.setUserPassword("oldpassword");
        existingUser.setPermission(Permission.Employee);
        existingUser.setEmail("old@example.com");

        UserRequest request = new UserRequest(
            "newname",
            "newpassword",
            Permission.Manager,
            "new@example.com",
            null
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");

        // When
        User result = userService.update(request, 1L);

        // Then
        assertEquals("newname", result.getUserName());
        assertEquals("encodedNewPassword", result.getUserPassword());
        assertEquals(Permission.Manager, result.getPermission());
        assertEquals("new@example.com", result.getEmail());
        verify(userRepository).findById(1L);
        verify(passwordEncoder).encode("newpassword");
    }

    @Test
    void updateThrowsRuntimeExceptionWhenUserNotFound() {
        // Given
        UserRequest request = new UserRequest(
            "newname",
            "newpassword",
            Permission.Manager,
            "new@example.com",
            null
        );

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        assertThrows(RuntimeException.class, () -> userService.update(request, 999L));

        // Then
        verify(userRepository).findById(999L);
        verify(passwordEncoder, never()).encode(anyString());
    }

    // ===== delete Tests =====

    @Test
    void deleteDeactivatesUserWhenItExists() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUserName("testuser");
        user.setActive(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        userService.delete(1L);

        // Then
        assertFalse(user.isActive());
        verify(userRepository).findById(1L);
    }

    @Test
    void deleteThrowsRuntimeExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        assertThrows(RuntimeException.class, () -> userService.delete(999L));

        // Then
        verify(userRepository).findById(999L);
    }

    // ===== findAllActive Tests =====

    @Test
    void findAllActiveReturnsOnlyActiveUsers() {
        // Given
        User user1 = new User();
        user1.setActive(true);
        User user2 = new User();
        user2.setActive(true);

        List<User> activeUsers = List.of(user1, user2);
        when(userRepository.findByActiveTrue()).thenReturn(activeUsers);

        // When
        List<User> result = userService.findAllActive();

        // Then
        assertSame(activeUsers, result);
        assertEquals(2, result.size());
        verify(userRepository).findByActiveTrue();
    }

    @Test
    void findAllActiveThrowsRuntimeExceptionWhenRepositoryFails() {
        // Given
        when(userRepository.findByActiveTrue()).thenThrow(new RuntimeException("Database error"));

        // When
        assertThrows(RuntimeException.class, () -> userService.findAllActive());

        // Then
        verify(userRepository).findByActiveTrue();
    }

    // ===== findByUserName Tests =====

    @Test
    void findByUserNameReturnsUserWhenItExists() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUserName("testuser");

        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(user));

        // When
        User result = userService.findByUserName("testuser");

        // Then
        assertSame(user, result);
        assertEquals("testuser", result.getUserName());
        verify(userRepository).findByUserName("testuser");
    }

    @Test
    void findByUserNameThrowsRuntimeExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findByUserName("nonexistent")).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.findByUserName("nonexistent"));

        // Then
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof ResponseStatusException);
        verify(userRepository).findByUserName("nonexistent");
    }

    // ===== findByEmail Tests =====

    @Test
    void findByEmailReturnsUserWhenItExists() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // When
        User result = userService.findByEmail("test@example.com");

        // Then
        assertSame(user, result);
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void findByEmailThrowsRuntimeExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.findByEmail("nonexistent@example.com"));

        // Then
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof ResponseStatusException);
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    // ===== authenticate Tests =====

    @Test
    void authenticateReturnsTrueWhenPasswordMatches() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setUserPassword("encodedPassword");

        AuthenticateRequest request = new AuthenticateRequest("test@example.com", "password123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        // When
        Boolean result = userService.authenticate(request);

        // Then
        assertTrue(result);
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123", "encodedPassword");
    }

    @Test
    void authenticateReturnsFalseWhenPasswordDoesNotMatch() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setUserPassword("encodedPassword");

        AuthenticateRequest request = new AuthenticateRequest("test@example.com", "wrongpassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        // When
        Boolean result = userService.authenticate(request);

        // Then
        assertFalse(result);
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("wrongpassword", "encodedPassword");
    }

    @Test
    void authenticateThrowsRuntimeExceptionWhenUserNotFound() {
        // Given
        AuthenticateRequest request = new AuthenticateRequest("nonexistent@example.com", "password123");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.authenticate(request));

        // Then
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof ResponseStatusException);
        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    // ===== changePassword Tests =====

    @Test
    void changePasswordUpdatesPasswordWhenOldPasswordDoesNotMatch() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setUserPassword("encodedOldPassword");

        ChangePasswordRequest request = new ChangePasswordRequest("test@example.com", "newPassword123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("encodedOldPassword", "newPassword123")).thenReturn(false);
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");

        // When
        BooleanRequest result = userService.changePassword(request);

        // Then
        assertTrue(result.bool());
        assertEquals("Senha redefinida com sucesso", result.message());
        assertEquals("encodedNewPassword", user.getUserPassword());
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("encodedOldPassword", "newPassword123");
        verify(passwordEncoder).encode("newPassword123");
    }

    @Test
    void changePasswordFailsWhenOldPasswordMatchesNewPassword() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setUserPassword("encodedPassword");

        ChangePasswordRequest request = new ChangePasswordRequest("test@example.com", "samePassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("encodedPassword", "samePassword")).thenReturn(true);

        // When
        BooleanRequest result = userService.changePassword(request);

        // Then
        assertFalse(result.bool());
        assertEquals("erro com a senha", result.message());
        assertEquals("encodedPassword", user.getUserPassword());
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("encodedPassword", "samePassword");
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void changePasswordThrowsRuntimeExceptionWhenUserNotFound() {
        // Given
        ChangePasswordRequest request = new ChangePasswordRequest("nonexistent@example.com", "newPassword");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.changePassword(request));

        // Then
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof ResponseStatusException);
        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
    }
}
