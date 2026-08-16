package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.AuthenticatedUserDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.AuthenticateRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.UserRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.UserDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/User")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticatedUserDto> authenticate(
            @RequestBody AuthenticateRequest request,
            HttpServletRequest servletRequest,
            HttpSession session
    ) {
        try {
            AuthenticatedUserDto user = userService.authenticate(request.email(), request.password());
            servletRequest.changeSessionId();
            session.setAttribute("userId", user.id());
            session.setAttribute("permission", user.permission());
            session.setAttribute("employeeId", user.employeeId());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/session")
    public ResponseEntity<AuthenticatedUserDto> session(HttpSession session) {
        Object userId = session.getAttribute("userId");
        if (!(userId instanceof Long id)) {
            return ResponseEntity.status(401).build();
        }

        try {
            return ResponseEntity.ok(AuthenticatedUserDto.toDto(userService.findEntityById(id)));
        } catch (Exception e) {
            session.invalidate();
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<UserDto>> findAll() {
        try {
            return ResponseEntity.ok(userService.findAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findId/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(userService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody UserRequest userRequest) {
        try {
            return ResponseEntity.ok(userService.save(userRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<String> update(@RequestBody UserRequest userRequest, @PathVariable long id) {
        try {
            return ResponseEntity.ok(userService.update(userRequest, id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        try {
            return ResponseEntity.ok(userService.delete(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/updatePatch/{id}")
    public ResponseEntity<String> updatePartial(@PathVariable long id, @RequestBody Map<String, Object> user) {
        try {
            String message = userService.applyPartialUpdate(id, user);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findAll/active")
    public ResponseEntity<List<UserDto>> findAllActive() {
        try {
            return ResponseEntity.ok(userService.findAllActive());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findByUserName/{userName}")
    public ResponseEntity<UserDto> findByUserName(@PathVariable String userName) {
        try {
            return ResponseEntity.ok(userService.findByUserName(userName));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PatchMapping("/change/password/{id}")
    public ResponseEntity<String> changePassword(@PathVariable long id , @RequestBody String newPassword){
        try {
            return ResponseEntity.ok(userService.changePassword(id , newPassword));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
