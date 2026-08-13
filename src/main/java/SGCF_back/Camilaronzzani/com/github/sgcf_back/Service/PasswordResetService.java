package SGCF_back.Camilaronzzani.com.github.sgcf_back.Service;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.PasswordResetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetService {
    @Autowired
    private PasswordResetRepository passwordResetRepository;
}
