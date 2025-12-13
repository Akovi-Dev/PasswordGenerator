package com.passwordGenerator.core;

import com.passwordGenerator.exceptions.InvalidPasswordConfigException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционные тесты для PasswordGenerator и PasswordGenerationConfig.
 * Тестируют совместную работу компонентов в реальных сценариях.
 *
 * @author Test Suite
 * @see PasswordGenerator
 * @see PasswordGenerationConfig
 */
@DisplayName("Password Generation Integration Tests")
public class PasswordGenerationIntegrationTest {

    @Test
    @DisplayName("Полный цикл: создание конфигурации и генерация пароля")
    void testFullPasswordGenerationCycle() throws InvalidPasswordConfigException {
        PasswordGenerationConfig config = new PasswordGenerationConfig(100);
        config.setUseLatin(true);
        config.setUseDigits(true);
        config.addRequiredCharacter('@');

        PasswordGenerator generator = new PasswordGenerator();
        String password = generator.generate(config);

        assertEquals(100, password.length());
        assertTrue(password.contains("@"));
        assertTrue(password.matches(".*[a-zA-Z].*"));
        assertTrue(password.matches(".*\\d.*"));
    }

    @Test
    @DisplayName("Генерирует пароль с кириллицей и спецсимволами")
    void testPasswordWithCyrillicAndSpecialChars() throws InvalidPasswordConfigException {
        PasswordGenerationConfig config = new PasswordGenerationConfig(80);
        config.setUseCyrillic(true);
        config.setUseSpecial(true);
        config.addRequiredCharacter('!');

        PasswordGenerator generator = new PasswordGenerator();
        String password = generator.generate(config);

        assertEquals(80, password.length());
        assertTrue(password.contains("!"));
    }

    @Test
    @DisplayName("Генерирует несколько разных паролей с одной конфигурацией")
    void testGenerateMultipleDifferentPasswords() throws InvalidPasswordConfigException {
        PasswordGenerationConfig config = new PasswordGenerationConfig(50);
        config.setUseLatin(true);
        config.setUseDigits(true);
        config.setUseSpecial(true);

        PasswordGenerator generator = new PasswordGenerator();

        String pwd1 = generator.generate(config);
        String pwd2 = generator.generate(config);
        String pwd3 = generator.generate(config);

        assertEquals(50, pwd1.length());
        assertEquals(50, pwd2.length());
        assertEquals(50, pwd3.length());
        assertNotEquals(pwd1, pwd2);
        assertNotEquals(pwd2, pwd3);
    }

    @Test
    @DisplayName("Обрабатывает очень короткие пароли")
    void testVeryShortPassword() throws InvalidPasswordConfigException {
        PasswordGenerationConfig config = new PasswordGenerationConfig(1);
        config.setUseLatin(true);

        PasswordGenerator generator = new PasswordGenerator();
        String password = generator.generate(config);

        assertEquals(1, password.length());
        assertTrue(password.matches("[a-zA-Z]"));
    }

    @Test
    @DisplayName("Обрабатывает большие пароли (100K символов)")
    void testLargePassword() throws InvalidPasswordConfigException {
        PasswordGenerationConfig config = new PasswordGenerationConfig(100_000);
        config.setUseLatin(true);

        PasswordGenerator generator = new PasswordGenerator();
        String password = generator.generate(config);

        assertEquals(100_000, password.length());
    }

    @Test
    @DisplayName("Все требуемые символы присутствуют в пароле")
    void testAllRequiredCharactersPresent() throws InvalidPasswordConfigException {
        PasswordGenerationConfig config = new PasswordGenerationConfig(50);
        config.setUseLatin(true);
        config.addRequiredCharacter('X');
        config.addRequiredCharacter('Y');
        config.addRequiredCharacter('Z');

        PasswordGenerator generator = new PasswordGenerator();
        String password = generator.generate(config);

        assertTrue(password.contains("X"));
        assertTrue(password.contains("Y"));
        assertTrue(password.contains("Z"));
        assertEquals(50, password.length());
    }

    @Test
    @DisplayName("Конфигурация equality влияет на результаты")
    void testConfigEqualityImpact() throws InvalidPasswordConfigException {
        PasswordGenerationConfig config1 = new PasswordGenerationConfig(50);
        config1.setUseLatin(true);
        config1.setUseDigits(true);

        PasswordGenerationConfig config2 = new PasswordGenerationConfig(50);
        config2.setUseLatin(true);
        config2.setUseDigits(true);

        assertEquals(config1, config2);

        PasswordGenerator generator = new PasswordGenerator();
        String pwd1 = generator.generate(config1);
        String pwd2 = generator.generate(config2);

        assertEquals(50, pwd1.length());
        assertEquals(50, pwd2.length());
    }

    @Test
    @DisplayName("Пароль содержит все выбранные типы символов")
    void testPasswordContainsAllSelectedCharacterTypes() throws InvalidPasswordConfigException {
        PasswordGenerationConfig config = new PasswordGenerationConfig(200);
        config.setUseLatin(true);
        config.setUseCyrillic(true);
        config.setUseDigits(true);
        config.setUseSpecial(true);

        PasswordGenerator generator = new PasswordGenerator();
        String password = generator.generate(config);

        assertEquals(200, password.length());
        assertTrue(password.matches(".*[a-zA-Z].*") || password.matches(".*[а-яА-ЯёЁ].*"));
        assertTrue(password.matches(".*\\d.*") || password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?].*"));
    }
}
