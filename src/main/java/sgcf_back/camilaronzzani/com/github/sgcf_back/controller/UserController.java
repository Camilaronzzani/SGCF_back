package sgcf_back.camilaronzzani.com.github.sgcf_back.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.AuthResponse;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.BooleanRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.AuthenticateRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.ChangePasswordRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.PasswordResetRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.UserRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.UserDto;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Permission;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.User;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")
    @GetMapping("/findAll")
    public ResponseEntity<List<UserDto>> findAll() {
            List<UserDto> userDtos = userService.findAll()
                    .stream()
                    .map(UserDto :: toDto)
                    .toList();
            return ResponseEntity.ok(userDtos);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")
    @GetMapping("/findId/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable long id) {
            return ResponseEntity.ok(UserDto.toDto(userService.findById(id)));
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @PostMapping("/save")
    public ResponseEntity<String> save(@Valid @RequestBody UserRequest userRequest) {
            userService.save(userRequest);
            return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")
    @PutMapping("/update/{id}")
    public ResponseEntity<UserDto> update(@Valid @RequestBody UserRequest userRequest, @PathVariable long id) {
            UserDto userDto = UserDto.toDto(userService.update(userRequest, id));
            return ResponseEntity.ok(userDto);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
            userService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")
    @GetMapping("/findAll/active")
    public ResponseEntity<List<UserDto>> findAllActive() {
            List<UserDto> userDtos = userService.findAllActive()
                    .stream()
                    .map(UserDto ::toDto)
                    .toList();
            return ResponseEntity.ok(userDtos);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")
    @GetMapping("/findByUserName/{userName}")
    public ResponseEntity<UserDto> findByUserName(@PathVariable String userName) {
            return ResponseEntity.ok(UserDto.toDto(userService.findByUserName(userName)));
    }
    @PreAuthorize("permitAll()")
    @PatchMapping("/change")
    public ResponseEntity<BooleanRequest> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest){
            return new ResponseEntity<>(userService.changePassword(changePasswordRequest),HttpStatus.OK);
    }
    @PreAuthorize("permitAll()")
    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(@Valid @RequestBody AuthenticateRequest authenticateRequest) {
        try {

        return ResponseEntity.ok(userService.authenticate(authenticateRequest));

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")
    @PostMapping("is-manager")
    public ResponseEntity<UserDto> isManager(@AuthenticationPrincipal User logged){
        User user = userService.findByEmail(logged.getEmail());
        return ResponseEntity.ok(UserDto.toDto(user));
    }
}
