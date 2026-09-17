package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Permission;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

public record UserRequest (

    @NotEmpty
    @NotBlank
     String userName,

    @NotEmpty
    @NotBlank
     String userPassword,


     Permission permission,

    @NotEmpty
    @Email
     String email,

    @Positive
     Long employeeId
){
    public static User toUser(UserRequest userRequest) {
        User user = new User();
        user.setUserName(userRequest.userName());
        user.setUserPassword(userRequest.userPassword());
        user.setPermission(userRequest.permission());
        user.setEmail(userRequest.email());
        user.setActive(true);
        return user;
    }
}
