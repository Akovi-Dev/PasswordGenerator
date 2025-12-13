package com.passwordGenerator.core;

import com.passwordGenerator.exceptions.InvalidPasswordConfigException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты для класса PasswordGenerator.
 * Тестируют генерацию паролей с различными конфигурациями и валидацией.
 *
 * @author Test Suite
 * @see PasswordGenerator
 * @see PasswordGenerationConfig
 */
@DisplayName("PasswordGenerator Unit Tests")
public class PasswordGeneratorTest {

    private PasswordGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new PasswordGenerator();
    }

    @Test
    @DisplayName("Генерирует пароль корректной длины с латиницей")
    void testGeneratePasswordWithCorrectLengthLatin() throws InvalidPasswordConfigException {
        PasswordGenerationConfig config = new PasswordGenerationConfig(50);
        config.setUseLatin(true);

        String password = generator.generate(config);

        assertEquals(50, password.length(), "Длина пароля должна быть 50");
        assertTrue(password.matches(".*[a-zA-Z].*"), "Пароль должен содержать латиницу");
    }

    @Test
    @DisplayName("Генерирует пароль корректной длины с кириллицей")
    void testGeneratePasswordWithCorrectLengthCyrillic() throws InvalidPasswordConfigException {
        PasswordGenerationConfig config = new PasswordGenerationConfig(50);
        config.setUseCyrillic(true);

        String password = generator.generate(config);

        assertEquals(50, password.length(), "Длина пароля должна быть 50");
        assertNotNull(password);
    }

    @Test
    @DisplayName("Генерирует пароль с цифрами")
    void testGeneratePasswordWithDigits() throws InvalidPasswordConfigException {
        PasswordGenerationConfig config = new PasswordGenerationConfig(50);
        config.setUseLatin(true);
        config.setUseDigits(true);

        String password = generator.generate(config);

        assertEquals(50, password.length());
        assertTrue(password.matches(".*\\d.*"), "Пароль должен содержать цифры");
    }

    @Test
    @DisplayName("Генерирует пароль со специальными символами")
    void testGeneratePasswordWithSpecialChars() throws InvalidPasswordConfigException {
        PasswordGenerationConfig config = new PasswordGenerationConfig(50);
        config.setUseLatin(true);
        config.setUseSpecial(true);

        String password = generator.generate(config);

        assertEquals(50, password.length());
        assertTrue(password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?].*"),
                "Пароль должен содержать спецсимволы");
    }

    @Test
    @DisplayName("Генерирует разные пароли при повторных вызовах")
    void testGeneratePasswordRandomness() throws InvalidPasswordConfigException {
        PasswordGenerationConfig config = new PasswordGenerationConfig(100);
        config.setUseLatin(true);
        config.setUseDigits(true);

        String password1 = generator.generate(config);
        String password2 = generator.generate(config);

        assertNotEquals(password1, password2, "Пароли должны быть разными (неопределённость)");
    }

    @Test
    @DisplayName("Включает требуемые символы в пароль")
    void testGeneratePasswordWithRequiredCharacters() throws InvalidPasswordConfigException {
        PasswordGenerationConfig config = new PasswordGenerationConfig(50);
        config.setUseLatin(true);
        config.setUseDigits(true);
        config.addRequiredCharacter('X');
        config.addRequiredCharacter('9');

        String password = generator.generate(config);

        assertEquals(50, password.length());
        assertTrue(password.contains("X"), "Пароль должен содержать X");
        assertTrue(password.contains("9"), "Пароль должен содержать 9");
    }

    @Test
    @DisplayName("Выбрасывает исключение при нулевой длине")
    void testGeneratePasswordWithZeroLength() {
        PasswordGenerationConfig config = new PasswordGenerationConfig(100);
        config.setUseLatin(true);

        assertThrows(IllegalArgumentException.class,
                () -> config.setLength(0),
                "Должно быть выброшено исключение при нулевой длине");
    }

    @Test
    @DisplayName("Выбрасывает исключение при отрицательной длине")
    void testGeneratePasswordWithNegativeLength() {
        PasswordGenerationConfig config = new PasswordGenerationConfig(100);
        config.setUseLatin(true);

        assertThrows(IllegalArgumentException.class,
                () -> config.setLength(-10),
                "Должно быть выброшено исключение при отрицательной длине");
    }

    @Test
    @DisplayName("Выбрасывает исключение при отсутствии типов символов")
    void testGeneratePasswordWithoutAnyCharacterType() {
        PasswordGenerationConfig config = new PasswordGenerationConfig(50);

        assertThrows(InvalidPasswordConfigException.class,
                () -> generator.generate(config),
                "Должно быть выброшено исключение если не выбран ни один тип символов");
    }

    @Test
    @DisplayName("Выбрасывает исключение если требуемых символов больше чем длина")
    void testGeneratePasswordRequiredCharactersExceedLength() {
        PasswordGenerationConfig config = new PasswordGenerationConfig(5);
        config.setUseLatin(true);
        config.addRequiredCharacter('A');
        config.addRequiredCharacter('B');
        config.addRequiredCharacter('C');
        config.addRequiredCharacter('D');
        config.addRequiredCharacter('E');

        assertThrows(IllegalArgumentException.class,
                () -> config.addRequiredCharacter('F'),
                "Должно быть выброшено исключение когда требуемых >= длина");
    }

    @Test
    @DisplayName("Генерирует пароль с максимальной длиной")
    void testGeneratePasswordWithMaxLength() throws InvalidPasswordConfigException {
        PasswordGenerationConfig config = new PasswordGenerationConfig(1_000_000);
        config.setUseLatin(true);

        String password = generator.generate(config);

        assertEquals(1_000_000, password.length());
        assertFalse(password.isEmpty());
    }

    @Test
    @DisplayName("Выбрасывает исключение при превышении максимальной длины")
    void testGeneratePasswordExceedsMaxLength() {
        PasswordGenerationConfig config = new PasswordGenerationConfig(1_000_001);
        config.setUseLatin(true);

        assertThrows(InvalidPasswordConfigException.class,
                () -> generator.generate(config),
                "Должно быть выброшено исключение при превышении максимальной длины");
    }

    @Test
    @DisplayName("Генерирует пароль с комбинацией всех типов символов")
    void testGeneratePasswordWithAllCharacterTypes() throws InvalidPasswordConfigException {
        PasswordGenerationConfig config = new PasswordGenerationConfig(100);
        config.setUseLatin(true);
        config.setUseCyrillic(true);
        config.setUseDigits(true);
        config.setUseSpecial(true);

        String password = generator.generate(config);

        assertEquals(100, password.length());
        assertNotNull(password);
    }

    @Test
    @DisplayName("Генерирует пароль малой длины (10 символов)")
    void testGeneratePasswordWithSmallLength() throws InvalidPasswordConfigException {
        PasswordGenerationConfig config = new PasswordGenerationConfig(10);
        config.setUseLatin(true);

        String password = generator.generate(config);

        assertEquals(10, password.length());
        assertTrue(password.matches(".*[a-zA-Z].*"));
    }
}
