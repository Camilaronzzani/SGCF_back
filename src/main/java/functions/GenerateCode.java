package functions;

import java.security.SecureRandom;

public class GenerateCode {

    private GenerateCode() {
    }

    public static String generateCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
