package SGCF_back.Camilaronzzani.com.github.sgcf_back.ControllerTest;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.UserController;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.BooleanRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.AuthenticateRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.ChangePasswordRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.PasswordResetRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.UserRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Employee;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Permission;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.User;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    private User sampleUser;
    private UserRequest sampleUserRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();

        Employee sampleEmployee = new Employee();
        sampleEmployee.setId(1L);

        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUserName("johndoe");
        sampleUser.setUserPassword("password123");
        sampleUser.setEmail("john@example.com");
        sampleUser.setPermission(Permission.Manager);
        sampleUser.setEmployee(sampleEmployee);
        sampleUser.setActive(true);

        sampleUserRequest = new UserRequest(
                "johndoe",
                "password123",
                Permission.Manager,
                "john@example.com",
                1L
        );
    }

    @Test
    @DisplayName("GET /api/user/findAll - Should return list of users")
    void findAll_ShouldReturnList() throws Exception {
        when(userService.findAll()).thenReturn(List.of(sampleUser));

        mockMvc.perform(get("/api/user/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userName").value("johndoe"));

        verify(userService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /api/user/findId/{id} - Should return user DTO by ID")
    void findById_ShouldReturnUser() throws Exception {
        when(userService.findById(1L)).thenReturn(sampleUser);

        mockMvc.perform(get("/api/user/findId/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("johndoe"));

        verify(userService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("POST /api/user/save - Should create user and return 201 Created")
    void save_ShouldReturnCreated() throws Exception {
        doNothing().when(userService).save(any(UserRequest.class));

        mockMvc.perform(post("/api/user/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleUserRequest)))
                .andExpect(status().isCreated());

        verify(userService, times(1)).save(any(UserRequest.class));
    }

    @Test
    @DisplayName("PUT /api/user/update/{id} - Should update user")
    void update_ShouldReturnUpdatedUser() throws Exception {
        when(userService.update(any(UserRequest.class), eq(1L))).thenReturn(sampleUser);

        mockMvc.perform(put("/api/user/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("johndoe"));

        verify(userService, times(1)).update(any(UserRequest.class), eq(1L));
    }

    @Test
    @DisplayName("DELETE /api/user/delete/{id} - Should delete user and return 204 No Content")
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/user/delete/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("GET /api/user/findAll/active - Should return active users")
    void findAllActive_ShouldReturnList() throws Exception {
        when(userService.findAllActive()).thenReturn(List.of(sampleUser));

        mockMvc.perform(get("/api/user/findAll/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(userService, times(1)).findAllActive();
    }

    @Test
    @DisplayName("GET /api/user/findByUserName/{userName} - Should return user by username")
    void findByUserName_ShouldReturnUser() throws Exception {
        when(userService.findByUserName("johndoe")).thenReturn(sampleUser);

        mockMvc.perform(get("/api/user/findByUserName/{userName}", "johndoe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("johndoe"));

        verify(userService, times(1)).findByUserName("johndoe");
    }

    @Test
    @DisplayName("PATCH /api/user/change - Should change password")
    void changePassword_ShouldReturnBooleanResponse() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("john@example.com", "newPassword123");
        BooleanRequest booleanResponse = new BooleanRequest(true, "Password changed successfully");

        when(userService.changePassword(any(ChangePasswordRequest.class))).thenReturn(booleanResponse);

        mockMvc.perform(patch("/api/user/change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bool").value(true))
                .andExpect(jsonPath("$.message").value("Password changed successfully"));

        verify(userService, times(1)).changePassword(any(ChangePasswordRequest.class));
    }

    @Test
    @DisplayName("POST /api/user/authenticate - Successful authentication populates session")
    void authenticate_Successful_ShouldSetSessionAndReturnOk() throws Exception {
        AuthenticateRequest authReq = new AuthenticateRequest("john@example.com", "password123");
        MockHttpSession session = new MockHttpSession();

        when(userService.authenticate(any(AuthenticateRequest.class))).thenReturn(true);
        when(userService.findByEmail("john@example.com")).thenReturn(sampleUser);

        mockMvc.perform(post("/api/user/authenticate")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        assertEquals(1L, session.getAttribute("userId"));
        assertEquals(Permission.Manager, session.getAttribute("permission"));
        assertEquals(1L, session.getAttribute("employeeId"));
    }

    @Test
    @DisplayName("POST /api/user/authenticate - Failed authentication returns 401 Unauthorized")
    void authenticate_Failed_ShouldReturnUnauthorized() throws Exception {
        AuthenticateRequest authReq = new AuthenticateRequest("john@example.com", "wrongpass");

        when(userService.authenticate(any(AuthenticateRequest.class))).thenReturn(false);

        mockMvc.perform(post("/api/user/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authReq)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/user/is-manager - User with Manager permission returns 200 OK")
    void isManager_WithManagerPermission_ShouldReturnOk() throws Exception {
        PasswordResetRequest resetRequest = new PasswordResetRequest("john@example.com");

        when(userService.findByEmail("john@example.com")).thenReturn(sampleUser);

        mockMvc.perform(post("/api/user/is-manager")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("johndoe"));
    }

    @Test
    @DisplayName("POST /api/user/is-manager - User without Manager permission returns 401 Unauthorized")
    void isManager_WithoutManagerPermission_ShouldReturnUnauthorized() throws Exception {
        PasswordResetRequest resetRequest = new PasswordResetRequest("john@example.com");
        sampleUser.setPermission(Permission.Employee);

        when(userService.findByEmail("john@example.com")).thenReturn(sampleUser);

        mockMvc.perform(post("/api/user/is-manager")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetRequest)))
                .andExpect(status().isUnauthorized());
    }
}