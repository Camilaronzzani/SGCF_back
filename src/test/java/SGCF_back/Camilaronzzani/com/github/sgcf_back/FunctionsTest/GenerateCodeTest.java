package sgcf_back.camilaronzzani.com.github.sgcf_back.FunctionsTest;

import Functions.GenerateCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GenerateCodeTest {

    @Test
    @DisplayName("generateCode - Should return a 6-digit numeric string")
    void generateCode_ShouldReturnSixDigitString() {
        String code = GenerateCode.generateCode();

        assertNotNull(code);
        assertEquals(6, code.length());
        assertTrue(code.matches("\\d{6}"), "The code should contain only digits");
    }

    @Test
    @DisplayName("generateCode - Should generate a value between 100000 and 999999 inclusive")
    void generateCode_ShouldBeWithinExpectedRange() {
        String codeStr = GenerateCode.generateCode();
        int code = Integer.parseInt(codeStr);

        assertTrue(code >= 100000 && code <= 999999, "Generated code must be between 100000 and 999999");
    }

    @RepeatedTest(100)
    @DisplayName("generateCode - Repeated executions should consistently produce valid 6-digit codes")
    void generateCode_RepeatedCheck() {
        String codeStr = GenerateCode.generateCode();

        assertEquals(6, codeStr.length());
        int code = Integer.parseInt(codeStr);
        assertTrue(code >= 100000 && code <= 999999);
    }

    @Test
    @DisplayName("generateCode - Should produce random distinct values across multiple calls")
    void generateCode_ShouldProduceVariedCodes() {
        Set<String> generatedCodes = new HashSet<>();
        int iterations = 100;

        for (int i = 0; i < iterations; i++) {
            generatedCodes.add(GenerateCode.generateCode());
        }

        assertTrue(generatedCodes.size() > 1, "Generated codes should vary across invocations");
    }
}