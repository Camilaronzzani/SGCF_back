package sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto;

import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Permission;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.User;

public record UserDto(Long id, String userName, Permission permission, String email, Long employeeId) {
    public static UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUserName(),
                user.getPermission(),
                user.getEmail(),
                user.getEmployee() == null ? null : user.getEmployee().getId()
        );
    }
}
