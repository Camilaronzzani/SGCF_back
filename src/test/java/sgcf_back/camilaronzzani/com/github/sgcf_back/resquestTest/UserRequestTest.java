package sgcf_back.camilaronzzani.com.github.sgcf_back.resquestTest;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.UserRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.User;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private UserRequest createValidRequest() {
        return new UserRequest("maria", "senha123", Permission.values()[0], "maria@teste.com", 7L);
    }

    @Test
    void toUserMapsRequestFields() {
        UserRequest request = createValidRequest();

        User user = UserRequest.toUser(request);

        assertEquals("maria", user.getUserName());
        assertEquals("senha123", user.getUserPassword());
        assertEquals(request.permission(), user.getPermission());
        assertEquals("maria@teste.com", user.getEmail());
        assertTrue(user.isActive());
    }

    @Test
    void toUserDoesNotLinkEmployee() {
        User user = UserRequest.toUser(createValidRequest());

        assertNull(user.getEmployee());
    }

    @Test
    void validRequestHasNoViolations() {
        assertTrue(validator.validate(createValidRequest()).isEmpty());
    }

    @Test
    void blankUserNameIsRejected() {
        UserRequest request = new UserRequest("   ", "senha123", Permission.values()[0], "maria@teste.com", 7L);

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void emptyPasswordIsRejected() {
        UserRequest request = new UserRequest("maria", "", Permission.values()[0], "maria@teste.com", 7L);

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void invalidEmailIsRejected() {
        UserRequest request = new UserRequest("maria", "senha123", Permission.values()[0], "email-invalido", 7L);

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void negativeEmployeeIdIsRejected() {
        UserRequest request = new UserRequest("maria", "senha123", Permission.values()[0], "maria@teste.com", -1L);

        assertFalse(validator.validate(request).isEmpty());
    }
}