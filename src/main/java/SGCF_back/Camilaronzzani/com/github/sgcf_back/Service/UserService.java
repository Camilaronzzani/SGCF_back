package SGCF_back.Camilaronzzani.com.github.sgcf_back.Service;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.BooleanRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.ChangePasswordRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Employee;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.AuthenticateRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.UserRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.User;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import static SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.UserRequest.toUser;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmployeeRepository employeeRepository;


    public List<User> findAll() {
        try {
            log.info("Fetches user list");
            return userRepository.findAll();

        } catch (RuntimeException e) {
            log.error("Error in UserService.findAll", e);
            throw new RuntimeException(e);
        }
    }

    public User findById(long id) {
        try {
            User user = userRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user no find"));

            log.info("User {} found successfully" , id);
            return user;
        } catch (Exception e) {
            log.error("Error in UserService.findById", e);
            throw new RuntimeException(e);
        }
    }

    public void save(UserRequest userRequest) {
        try {
            User user = toUser(userRequest);
            Employee employee = employeeRepository.findById(userRequest.employeeId()).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "employee no find"));

            user.setEmployee(employee);
            user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
            userRepository.save(user);

            log.info("User {} saved successfully", user.getUserName());

        } catch (Exception e) {
            log.error("Error in UserService.save", e);
            throw new RuntimeException(e);
        }
    }



    public User changeDataByUser(long id, UserRequest newUser) {

        User userOld = userRepository.findById(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        userOld.setUserName(newUser.userName());
        userOld.setUserPassword(passwordEncoder.encode(newUser.userPassword()));
        userOld.setPermission(newUser.permission());
        userOld.setEmail(newUser.email());

        return userOld;
    }

    @Transactional
    public User update(UserRequest userRequest, long id) {
        try {
            User user = changeDataByUser(id, userRequest);

            log.info("User {} updated successfully", user.getUserName());
            return user;
        } catch (Exception e) {
            log.error("Error in UserService.update", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void delete(long id) {
        try {
            User user = userRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user no find"));
            user.setActive(false);

            log.info("User {} deactivated successfully", user.getUserName());
        } catch (Exception e) {
            log.error("Error in UserService.delete", e);
            throw new RuntimeException(e);
        }
    }

    public List<User> findAllActive() {
        try {
            log.info("Fetch User list active");
            return userRepository.findByActiveTrue();

        } catch (Exception e) {
            log.error("Error in UserService.findAllActive", e);
            throw new RuntimeException(e);
        }
    }

    public User findByUserName(String userName) {
        try {
            log.info("Fetch user for user name");

            return userRepository.findByUserName(userName).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
        } catch (Exception e) {
            log.error("Error in UserService.findByUserName", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public BooleanRequest changePassword(ChangePasswordRequest changePasswordRequest) {
        try {
            boolean boo;
            String message = "";


            User user = userRepository.findByEmail(changePasswordRequest.email()).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user no find"));

            if (passwordEncoder.matches(user.getUserPassword(), changePasswordRequest.password())){
                boo = false;
                message = "erro com a senha";
            }else {
                user.setUserPassword(passwordEncoder.encode(changePasswordRequest.password()));
                boo = true;
               message = "Senha redefinida com sucesso";
            }

            log.info("Password change successful ");
            return new BooleanRequest(boo , message);

        } catch (Exception e) {
            log.error("Error in UserService.changePassword", e);

            throw new RuntimeException(e);
        }
    }

    public Boolean authenticate(AuthenticateRequest authenticateRequest) {
        try {

            User user = userRepository.findByEmail(authenticateRequest.email()).orElseThrow(()
                    ->new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
            log.info("authenticating user");
            boolean boo = passwordEncoder.matches(authenticateRequest.password(), user.getUserPassword());
            return boo;
        } catch (Exception e) {
            log.error("Error in UserService.authenticate", e);
            throw new RuntimeException(e);
        }
    }

    public User findByEmail(String email) {
        try {
            log.info("Fetching user");
            return userRepository.findByEmail(email).orElseThrow(()
                    ->new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
        } catch (Exception e) {
            log.error("Error in UserService.findByEmail", e);
            throw new RuntimeException(e);
        }
    }
}
