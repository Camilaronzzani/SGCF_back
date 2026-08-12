package Controller;

import Service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/Password/Reset")
public class PasswordResetController {
    @Autowired
    private PasswordResetService passwordResetService;
}
