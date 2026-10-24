package sgcf_back.camilaronzzani.com.github.sgcf_back.repositoryTest;

import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.User;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Permission;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUserName("camila");
        user.setUserPassword("123456");
        user.setPermission(Permission.MANAGER);
        user.setEmail("camila@email.com");
        user.setActive(true);
        entityManager.persist(user);
    }

    @Test
    @DisplayName("Buscar usuário pelo email")
    void findByEmailReturnsUserWhenEmailExists() {
        Optional<User> result = userRepository.findByEmail("camila@email.com");

        assertTrue(result.isPresent());
        assertEquals("camila", result.get().getUserName());
    }

    @Test
    @DisplayName("Buscar usuário com email que não existe")
    void findByEmailReturnsEmptyWhenEmailDoesNotExist() {
        Optional<User> result = userRepository.findByEmail("naoexiste@email.com");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Buscar usuário pelo nome de usuário")
    void findByUserNameReturnsUserWhenUserNameExists() {
        Optional<User> result = userRepository.findByUserName("camila");

        assertTrue(result.isPresent());
        assertEquals(Permission.MANAGER, result.get().getPermission());
    }
}
