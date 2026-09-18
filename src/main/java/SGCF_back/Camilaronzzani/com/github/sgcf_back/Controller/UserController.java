package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.AuthResponse;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.BooleanRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.AuthenticateRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.ChangePasswordRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.PasswordResetRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.UserRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.UserDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Permission;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.User;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/findAll")
    public ResponseEntity<List<UserDto>> findAll() {
            List<UserDto> userDtos = userService.findAll()
                    .stream()
                    .map(UserDto :: toDto)
                    .toList();
            return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/findId/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable long id) {
            return ResponseEntity.ok(UserDto.toDto(userService.findById(id)));
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@Valid @RequestBody UserRequest userRequest) {
            userService.save(userRequest);
            return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserDto> update(@Valid @RequestBody UserRequest userRequest, @PathVariable long id) {
            UserDto userDto = UserDto.toDto(userService.update(userRequest, id));
            return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable long id) {
            userService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/findAll/active")
    public ResponseEntity<List<UserDto>> findAllActive() {
            List<UserDto> userDtos = userService.findAllActive()
                    .stream()
                    .map(UserDto ::toDto)
                    .toList();
            return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/findByUserName/{userName}")
    public ResponseEntity<UserDto> findByUserName(@PathVariable String userName) {
            return ResponseEntity.ok(UserDto.toDto(userService.findByUserName(userName)));
    }
    @PatchMapping("/change")
    public ResponseEntity<BooleanRequest> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest){
            return new ResponseEntity<>(userService.changePassword(changePasswordRequest),HttpStatus.OK);
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthenticateRequest authenticateRequest) {
        boolean isAuthenticated = userService.authenticate(authenticateRequest);

        if (!isAuthenticated) {
            return new ResponseEntity<>(new AuthResponse(false), HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok(new AuthResponse(true));
    }

    @PostMapping("is-manager")
    public ResponseEntity<UserDto> isManager(@Valid @RequestBody PasswordResetRequest email){
        User user = userService.findByEmail(email.email());
        if (user.getPermission() == Permission.Manager){
            return ResponseEntity.ok(UserDto.toDto(user));
        }
        return new ResponseEntity<>(UserDto.toDto(user), HttpStatus.UNAUTHORIZED);
    }
}
