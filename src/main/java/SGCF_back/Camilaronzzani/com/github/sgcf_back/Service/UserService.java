package SGCF_back.Camilaronzzani.com.github.sgcf_back.Service;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.UserRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.UserDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Permission;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.User;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<UserDto> findAll() {
        try {
            List<User> userList = userRepository.findAll();
            List<UserDto> userDtos = new ArrayList<>();
            userList.forEach(user -> {
                UserDto userDto = UserDto.toDto(user);
                userDtos.add(userDto);
            });
            return userDtos;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public UserDto findById(long id) {
        try {
            Optional<User> user = Optional.of(userRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user no find")));
            return UserDto.toDto(user.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String save(UserRequest userRequest) {
        try {
            User user = toUser(userRequest);
            userRepository.save(user);
            return "User: " + user.getUserName() + " save successful ";
        } catch (Exception e) {
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

    public void changeDataByUser(User userOld, User newUser) {
        userOld.setUserName(newUser.getUserName());
        userOld.setUserPassword(newUser.getUserPassword());
        userOld.setPermission(newUser.getPermission());
        userOld.setEmail(newUser.getEmail());
    }

    public String update(UserRequest userRequest, long id) {
        try {
            User user = toUser(userRequest);
            User userOld = Optional.of(userRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user no find"))).get();
            changeDataByUser(userOld, user);
            userRepository.save(userOld);
            return "User: " + userOld.getUserName() + " update successful ";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String delete(long id) {
        try {
            User user = userRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user no find"));
            user.setActive(false);
            userRepository.save(user);
            return "User: " + user.getUserName() + " delete successful ";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String applyPartialUpdate(long id, Map<String, Object> user) {
        try {
            User user1 = userRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user no find"));
            user.forEach((key, value) -> {
                switch (key) {
                    case "userName" -> user1.setUserName(value.toString());
                    case "userPassword" -> user1.setUserPassword(value.toString());
                    case "permission" -> user1.setPermission(Permission.valueOf(value.toString()));
                    case "email" -> user1.setEmail(value.toString());
                }
            });
            userRepository.save(user1);
            return "User: " + user1.getUserName() + " update successful ";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<UserDto> findAllActive() {
        try {
            List<User> userList = userRepository.findByActiveTrue();
            List<UserDto> userDtoList = new ArrayList<>();
            userList.forEach(user -> {
                UserDto userDto = UserDto.toDto(user);
                userDtoList.add(userDto);
            });
            return userDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public UserDto findByUserName(String userName) {
        try {
            User user = userRepository.findByUserName(userName).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user no find"));
            return UserDto.toDto(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String changePassword(long id, String newPassword) {
        try {

            User user = userRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user no find"));
            user.setUserPassword(newPassword);
            userRepository.save(user);
            return "Password change successful ";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
