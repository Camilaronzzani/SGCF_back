package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Permission;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.User;

public record UserDto(Long id, String userName, Permission permission, String email) {
    public static UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUserName(),
                user.getPermission(),
                user.getEmail()
        );
    }
}
