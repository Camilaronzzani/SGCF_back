package SGCF_back.Camilaronzzani.com.github.sgcf_back.Service;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.ChangePasswordRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Employee;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.AuthenticateRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.UserRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.UserDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Permission;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.User;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmployeeRepository employeeRepository;


    public List<UserDto> findAll() {
        try {
            log.info("Fetches user list");
            return userRepository.findAll()
                    .stream()
                    .map(UserDto :: toDto)
                    .toList();

        } catch (RuntimeException e) {
            log.error("Error in UserService.findAll", e);
            throw new RuntimeException(e);
        }
    }

    public UserDto findById(long id) {
        try {
            User user = userRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user no find"));

            log.info("User {} found successfully" , id);
            return UserDto.toDto(user);
        } catch (Exception e) {
            log.error("Error in UserService.findById", e);
            throw new RuntimeException(e);
        }
    }

    public String save(UserRequest userRequest) {
        try {
            User user = toUser(userRequest);
            Employee employee = employeeRepository.findById(userRequest.getEmployeeId()).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "employee no find"));

            user.setEmployee(employee);
            user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
            userRepository.save(user);

            log.info("User {} saved successfully", user.getUserName());
            return "User: " + user.getUserName() + " saved successfully ";

        } catch (Exception e) {
            log.error("Error in UserService.save", e);
            throw new RuntimeException(e);
        }
    }

    public User toUser(UserRequest userRequest) {
        User user = new User();
        user.setUserName(userRequest.getUserName());
        user.setUserPassword(userRequest.getUserPassword());
        user.setPermission(userRequest.getPermission());
        user.setEmail(userRequest.getEmail());
        user.setActive(true);
        return user;
    }

    public User changeDataByUser(long id, UserRequest newUser) {

        User userOld = userRepository.findById(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        userOld.setUserName(newUser.getUserName());
        userOld.setUserPassword(passwordEncoder.encode(newUser.getUserPassword()));
        userOld.setPermission(newUser.getPermission());
        userOld.setEmail(newUser.getEmail());

        return userOld;
    }

    @Transactional
    public String update(UserRequest userRequest, long id) {
        try {
            User user = changeDataByUser(id, userRequest);

            log.info("User {} updated successfully", user.getUserName());
            return "User: " + user.getUserName() + " updated successfully ";
        } catch (Exception e) {
            log.error("Error in UserService.update", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public String delete(long id) {
        try {
            User user = userRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user no find"));
            user.setActive(false);

            log.info("User {} deactivated successfully", user.getUserName());
            return "User: " + user.getUserName() + " deactivated successfully ";
        } catch (Exception e) {
            log.error("Error in UserService.delete", e);
            throw new RuntimeException(e);
        }
    }

    //delete
    public String applyPartialUpdate(long id, Map<String, Object> user) {
        try {
            User user1 = userRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user no find"));
            user.forEach((key, value) -> {
                switch (key) {
                    case "userName" -> user1.setUserName(value.toString());
                    case "permission" -> user1.setPermission(Permission.valueOf(value.toString()));
                    case "email" -> user1.setEmail(value.toString());
                }
            });
            userRepository.save(user1);
            return "User: " + user1.getUserName() + " updated successfully ";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<UserDto> findAllActive() {
        try {
            log.info("Fetch User list active");
            return userRepository.findByActiveTrue()
                    .stream()
                    .map(UserDto ::toDto)
                    .toList();

        } catch (Exception e) {
            log.error("Error in UserService.findAllActive", e);
            throw new RuntimeException(e);
        }
    }

    public UserDto findByUserName(String userName) {
        try {
            log.info("Fetch user for user name");
            User user = userRepository.findByUserName(userName).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

            return UserDto.toDto(user);
        } catch (Exception e) {
            log.error("Error in UserService.findByUserName", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public String changePassword(ChangePasswordRequest changePasswordRequest) {
        try {

            User user = userRepository.findByEmail(changePasswordRequest.getEmail()).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user no find"));
            user.setUserPassword(passwordEncoder.encode(changePasswordRequest.getPassword()));

            log.info("Password change successful ");
            return "Password change successful ";

        } catch (Exception e) {
            log.error("Error in UserService.changePassword", e);

            throw new RuntimeException(e);
        }
    }

    public Boolean authenticate(AuthenticateRequest authenticateRequest) {
        try {

            User user = userRepository.findByEmail(authenticateRequest.getEmail()).orElseThrow(()
                    ->new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
            log.info("authenticating user");
            return passwordEncoder.matches(authenticateRequest.getPassword(), user.getUserPassword());
        } catch (Exception e) {
            log.error("Error in UserService.authenticate", e);
            throw new RuntimeException(e);
        }
    }
}
