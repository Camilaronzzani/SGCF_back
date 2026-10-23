package sgcf_back.camilaronzzani.com.github.sgcf_back.ControllerTest;

import sgcf_back.camilaronzzani.com.github.sgcf_back.config.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.UserController;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.BooleanRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.AuthenticateRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.ChangePasswordRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.PasswordResetRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.UserRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Employee;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Permission;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.User;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private ObjectMapper objectMapper;

    private User sampleUser;
    private UserRequest sampleUserRequest;

    @BeforeEach
    void setUp() {
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
    void findAllReturnsListOfUsers() throws Exception {
        when(userService.findAll()).thenReturn(List.of(sampleUser));

        ResultActions resposta = mockMvc.perform(get("/api/user/findAll"));

        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userName").value("johndoe"));
        verify(userService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /api/user/findId/{id} - Should return user DTO by ID")
    void findByIdReturnsUserWhenItExists() throws Exception {
        when(userService.findById(1L)).thenReturn(sampleUser);

        ResultActions resposta = mockMvc.perform(get("/api/user/findId/{id}", 1L));

        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("johndoe"));
        verify(userService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("POST /api/user/save - Should create user and return 201 Created")
    void saveReturns201WhenRequestIsValid() throws Exception {
        doNothing().when(userService).save(any(UserRequest.class));

        ResultActions resposta = mockMvc.perform(post("/api/user/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleUserRequest)));

        resposta.andExpect(status().isCreated());
        verify(userService, times(1)).save(any(UserRequest.class));
    }

    @Test
    @DisplayName("PUT /api/user/update/{id} - Should update user")
    void updateReturnsUpdatedUser() throws Exception {
        when(userService.update(any(UserRequest.class), eq(1L))).thenReturn(sampleUser);

        ResultActions resposta = mockMvc.perform(put("/api/user/update/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleUserRequest)));

        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("johndoe"));
        verify(userService, times(1)).update(any(UserRequest.class), eq(1L));
    }

    @Test
    @DisplayName("DELETE /api/user/delete/{id} - Should delete user and return 204 No Content")
    void deleteReturns204WhenUserIsDeleted() throws Exception {
        doNothing().when(userService).delete(1L);

        ResultActions resposta = mockMvc.perform(delete("/api/user/delete/{id}", 1L));

        resposta.andExpect(status().isNoContent());
        verify(userService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("GET /api/user/findAll/active - Should return active users")
    void findAllActiveReturnsListOfActiveUsers() throws Exception {
        when(userService.findAllActive()).thenReturn(List.of(sampleUser));

        ResultActions resposta = mockMvc.perform(get("/api/user/findAll/active"));

        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(userService, times(1)).findAllActive();
    }

    @Test
    @DisplayName("GET /api/user/findByUserName/{userName} - Should return user by username")
    void findByUserNameReturnsUserWhenItExists() throws Exception {
        when(userService.findByUserName("johndoe")).thenReturn(sampleUser);

        ResultActions resposta = mockMvc.perform(get("/api/user/findByUserName/{userName}", "johndoe"));

        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("johndoe"));
        verify(userService, times(1)).findByUserName("johndoe");
    }

    @Test
    @DisplayName("PATCH /api/user/change - Should change password")
    void changePasswordReturnsTrueWhenPasswordIsChanged() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("john@example.com", "newPassword123");
        BooleanRequest booleanResponse = new BooleanRequest(true, "Password changed successfully");

        when(userService.changePassword(any(ChangePasswordRequest.class))).thenReturn(booleanResponse);

        ResultActions resposta = mockMvc.perform(patch("/api/user/change")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.bool").value(true))
                .andExpect(jsonPath("$.message").value("Password changed successfully"));
        verify(userService, times(1)).changePassword(any(ChangePasswordRequest.class));
    }

    @Test
    @DisplayName("POST /api/user/authenticate - Successful authentication populates session")
    void authenticateReturns200AndFillsSessionWhenCredentialsAreValid() throws Exception {
        AuthenticateRequest authReq = new AuthenticateRequest("john@example.com", "password123");
        MockHttpSession session = new MockHttpSession();

        when(userService.authenticate(any(AuthenticateRequest.class))).thenReturn(true);
        when(userService.findByEmail("john@example.com")).thenReturn(sampleUser);

        ResultActions resposta = mockMvc.perform(post("/api/user/authenticate")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authReq)));

        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        assertEquals(1L, session.getAttribute("userId"));
        assertEquals(Permission.Manager, session.getAttribute("permission"));
        assertEquals(1L, session.getAttribute("employeeId"));
    }

    @Test
    @DisplayName("POST /api/user/authenticate - Failed authentication returns 401 Unauthorized")
    void authenticateReturns401WhenCredentialsAreInvalid() throws Exception {
        AuthenticateRequest authReq = new AuthenticateRequest("john@example.com", "wrongpass");

        when(userService.authenticate(any(AuthenticateRequest.class))).thenReturn(false);

        ResultActions resposta = mockMvc.perform(post("/api/user/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authReq)));

        resposta.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/user/is-manager - User with Manager permission returns 200 OK")
    void isManagerReturns200WhenUserIsManager() throws Exception {
        PasswordResetRequest resetRequest = new PasswordResetRequest("john@example.com");

        when(userService.findByEmail("john@example.com")).thenReturn(sampleUser);

        ResultActions resposta = mockMvc.perform(post("/api/user/is-manager")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetRequest)));

        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("johndoe"));
    }

    @Test
    @DisplayName("POST /api/user/is-manager - User without Manager permission returns 401 Unauthorized")
    void isManagerReturns401WhenUserIsNotManager() throws Exception {
        PasswordResetRequest resetRequest = new PasswordResetRequest("john@example.com");
        sampleUser.setPermission(Permission.Employee);

        when(userService.findByEmail("john@example.com")).thenReturn(sampleUser);

        ResultActions resposta = mockMvc.perform(post("/api/user/is-manager")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetRequest)));

        resposta.andExpect(status().isUnauthorized());
    }
}