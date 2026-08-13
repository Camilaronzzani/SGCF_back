package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Permission;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    private String userName;
    private String userPassword;
    private Permission permission;
    private String email;
}
