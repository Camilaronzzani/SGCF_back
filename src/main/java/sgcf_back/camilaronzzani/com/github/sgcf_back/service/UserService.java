package sgcf_back.camilaronzzani.com.github.sgcf_back.service;

import sgcf_back.camilaronzzani.com.github.sgcf_back.exception.ServiceException;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.BooleanRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.ChangePasswordRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Employee;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.AuthenticateRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.UserRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.User;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import static sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.UserRequest.toUser;

@Slf4j
@Service
public class UserService {

    private static final String USER_NOT_FOUND = "user not found";
    private static final String USER_NO_FIND = "user no find";
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
            throw new ServiceException(e);
        }
    }

    public User findById(long id) {
        try {
            User user = userRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NO_FIND));

            log.info("User {} found successfully" , id);
            return user;
        } catch (Exception e) {
            log.error("Error in UserService.findById", e);
            throw new ServiceException(e);
        }
    }

    public void save(UserRequest userRequest) {
            if (userRequest.permission() == sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Permission.Employee
                    && userRequest.employeeId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "employeeId is required for employee accounts");
            }

        try {
            User user = toUser(userRequest);
                Employee employee = userRequest.employeeId() == null
                        ? null
                        : employeeRepository.findById(userRequest.employeeId()).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "employee no find"));

                if (employee != null && userRepository.findByEmployeeId(employee.getId()).isPresent()) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "employee already has an account");
                }

            user.setEmployee(employee);
            user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
            userRepository.save(user);

            log.info("User {} saved successfully", user.getUserName());

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error in UserService.save", e);
            throw new ServiceException(e);
        }
    }



    public User changeDataByUser(long id, UserRequest newUser) {

        User userOld = userRepository.findById(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));

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
            throw new ServiceException(e);
        }
    }

    @Transactional
    public void delete(long id) {
        try {
            User user = userRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NO_FIND));
            user.setActive(false);

            log.info("User {} deactivated successfully", user.getUserName());
        } catch (Exception e) {
            log.error("Error in UserService.delete", e);
            throw new ServiceException(e);
        }
    }

    public List<User> findAllActive() {
        try {
            log.info("Fetch User list active");
            return userRepository.findByActiveTrue();

        } catch (Exception e) {
            log.error("Error in UserService.findAllActive", e);
            throw new ServiceException(e);
        }
    }

    public User findByUserName(String userName) {
        try {
            log.info("Fetch user for user name");

            return userRepository.findByUserName(userName).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
        } catch (Exception e) {
            log.error("Error in UserService.findByUserName", e);
            throw new ServiceException(e);
        }
    }

    @Transactional
    public BooleanRequest changePassword(ChangePasswordRequest changePasswordRequest) {
        try {
            boolean boo;
            String message = "";


            User user = userRepository.findByEmail(changePasswordRequest.email()).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NO_FIND));

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

            throw new ServiceException(e);
        }
    }

    public boolean authenticate(AuthenticateRequest authenticateRequest) {
        try {

            User user = userRepository.findByEmail(authenticateRequest.email()).orElseThrow(()
                    ->new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
            log.info("authenticating user");
            return passwordEncoder.matches(authenticateRequest.password(), user.getUserPassword());
        } catch (Exception e) {
            log.error("Error in UserService.authenticate", e);
            throw new ServiceException(e);
        }
    }

    public User findByEmail(String email) {
        try {
            log.info("Fetching user");
            return userRepository.findByEmail(email).orElseThrow(()
                    ->new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
        } catch (Exception e) {
            log.error("Error in UserService.findByEmail", e);
            throw new ServiceException(e);
        }
    }
}
