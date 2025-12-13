package com.passwordGenerator.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты для класса PasswordGenerationConfig.
 * Тестируют конфигурацию паролей: валидацию параметров, управление требуемыми символами.
 *
 * @author Test Suite
 * @see PasswordGenerationConfig
 */
@DisplayName("PasswordGenerationConfig Unit Tests")
public class PasswordGenerationConfigTest {

    private PasswordGenerationConfig config;

    @BeforeEach
    void setUp() {
        config = new PasswordGenerationConfig(50);
    }

    @Test
    @DisplayName("Конструктор инициализирует корректную длину")
    void testConstructorInitializesLength() {
        assertEquals(50, config.getLength(), "Длина должна быть 50");
    }

    @Test
    @DisplayName("Конструктор инициализирует все флаги в false")
    void testConstructorInitializesFlagsToFalse() {
        assertFalse(config.isUseLatin(), "useLatin должен быть false");
        assertFalse(config.isUseCyrillic(), "useCyrillic должен быть false");
        assertFalse(config.isUseDigits(), "useDigits должен быть false");
        assertFalse(config.isUseSpecial(), "useSpecial должен быть false");
    }

    @Test
    @DisplayName("Выбрасывает исключение при нулевой длине в конструкторе")
    void testConstructorThrowsExceptionOnZeroLength() {
        assertThrows(IllegalArgumentException.class,
                () -> new PasswordGenerationConfig(0),
                "Должно быть выброшено исключение при нулевой длине");
    }

    @Test
    @DisplayName("Выбрасывает исключение при отрицательной длине в конструкторе")
    void testConstructorThrowsExceptionOnNegativeLength() {
        assertThrows(IllegalArgumentException.class,
                () -> new PasswordGenerationConfig(-50),
                "Должно быть выброшено исключение при отрицательной длине");
    }

    @Test
    @DisplayName("Устанавливает использование латинских букв")
    void testSetUseLatin() {
        config.setUseLatin(true);
        assertTrue(config.isUseLatin(), "useLatin должен быть true");
        
        config.setUseLatin(false);
        assertFalse(config.isUseLatin(), "useLatin должен быть false");
    }

    @Test
    @DisplayName("Устанавливает использование кириллицы")
    void testSetUseCyrillic() {
        config.setUseCyrillic(true);
        assertTrue(config.isUseCyrillic(), "useCyrillic должен быть true");
        
        config.setUseCyrillic(false);
        assertFalse(config.isUseCyrillic(), "useCyrillic должен быть false");
    }

    @Test
    @DisplayName("Устанавливает использование цифр")
    void testSetUseDigits() {
        config.setUseDigits(true);
        assertTrue(config.isUseDigits(), "useDigits должен быть true");
        
        config.setUseDigits(false);
        assertFalse(config.isUseDigits(), "useDigits должен быть false");
    }

    @Test
    @DisplayName("Устанавливает использование специальных символов")
    void testSetUseSpecial() {
        config.setUseSpecial(true);
        assertTrue(config.isUseSpecial(), "useSpecial должен быть true");
        
        config.setUseSpecial(false);
        assertFalse(config.isUseSpecial(), "useSpecial должен быть false");
    }

    @Test
    @DisplayName("Изменяет длину пароля")
    void testSetLength() {
        config.setLength(100);
        assertEquals(100, config.getLength(), "Длина должна измениться на 100");
    }

    @Test
    @DisplayName("Выбрасывает исключение при установке нулевой длины")
    void testSetLengthThrowsOnZero() {
        assertThrows(IllegalArgumentException.class,
                () -> config.setLength(0),
                "Должно быть выброшено исключение при попытке установить нулевую длину");
    }

    @Test
    @DisplayName("Добавляет требуемый символ")
    void testAddRequiredCharacter() {
        config.addRequiredCharacter('A');
        assertTrue(config.getRequiredCharacters().contains('A'),
                "Символ A должен быть в требуемых");
    }

    @Test
    @DisplayName("Добавляет несколько требуемых символов")
    void testAddMultipleRequiredCharacters() {
        config.addRequiredCharacter('A');
        config.addRequiredCharacter('1');
        config.addRequiredCharacter('!');

        assertEquals(3, config.getRequiredCharacterCount(), "Должно быть 3 требуемых символа");
        assertTrue(config.getRequiredCharacters().contains('A'));
        assertTrue(config.getRequiredCharacters().contains('1'));
        assertTrue(config.getRequiredCharacters().contains('!'));
    }

    @Test
    @DisplayName("Выбрасывает исключение при null требуемом символе")
    void testAddRequiredCharacterThrowsOnNull() {
        assertThrows(NullPointerException.class,
                () -> config.addRequiredCharacter(null),
                "Должно быть выброшено исключение при null символе");
    }

    @Test
    @DisplayName("Выбрасывает исключение если требуемых символов >= длины")
    void testAddRequiredCharacterThrowsWhenExceedsLength() {
        PasswordGenerationConfig smallConfig = new PasswordGenerationConfig(2);
        smallConfig.addRequiredCharacter('A');
        smallConfig.addRequiredCharacter('B');

        assertThrows(IllegalArgumentException.class,
                () -> smallConfig.addRequiredCharacter('C'),
                "Должно быть выброшено исключение при превышении длины");
    }

    @Test
    @DisplayName("Возвращает количество требуемых символов")
    void testGetRequiredCharacterCount() {
        assertEquals(0, config.getRequiredCharacterCount(), "Должно быть 0 требуемых символов");
        
        config.addRequiredCharacter('X');
        assertEquals(1, config.getRequiredCharacterCount(), "Должно быть 1 требуемый символ");
    }

    @Test
    @DisplayName("Возвращает неизменяемый набор требуемых символов")
    void testGetRequiredCharactersIsUnmodifiable() {
        config.addRequiredCharacter('A');
        var characters = config.getRequiredCharacters();

        assertThrows(UnsupportedOperationException.class,
                () -> characters.add('B'),
                "Набор требуемых символов должен быть неизменяемым");
    }

    @Test
    @DisplayName("Возвращает максимальную длину пароля")
    void testGetMaxPasswordLength() {
        assertEquals(1_000_000, PasswordGenerationConfig.getMaxPasswordLength(),
                "Максимальная длина должна быть 1_000_000");
    }

    @Test
    @DisplayName("Конфигурации с одинаковыми параметрами равны")
    void testEqualsWithSameParameters() {
        PasswordGenerationConfig config1 = new PasswordGenerationConfig(50);
        PasswordGenerationConfig config2 = new PasswordGenerationConfig(50);
        config1.setUseLatin(true);
        config2.setUseLatin(true);

        assertEquals(config1, config2, "Конфигурации с одинаковыми параметрами должны быть равны");
    }

    @Test
    @DisplayName("Конфигурации с разными длинами не равны")
    void testNotEqualsWithDifferentLength() {
        PasswordGenerationConfig config1 = new PasswordGenerationConfig(50);
        PasswordGenerationConfig config2 = new PasswordGenerationConfig(100);
        config1.setUseLatin(true);
        config2.setUseLatin(true);

        assertNotEquals(config1, config2, "Конфигурации с разными длинами не должны быть равны");
    }

    @Test
    @DisplayName("Конфигурации с разными типами символов не равны")
    void testNotEqualsWithDifferentCharacterTypes() {
        PasswordGenerationConfig config1 = new PasswordGenerationConfig(50);
        PasswordGenerationConfig config2 = new PasswordGenerationConfig(50);
        config1.setUseLatin(true);
        config2.setUseCyrillic(true);

        assertNotEquals(config1, config2, "Конфигурации с разными типами символов не равны");
    }

    @Test
    @DisplayName("Одна и та же конфигурация равна себе")
    void testEqualsReflexive() {
        assertEquals(config, config, "Конфигурация должна быть равна себе");
    }

    @Test
    @DisplayName("Конфигурация не равна null")
    void testNotEqualsNull() {
        assertNotEquals(null, config, "Конфигурация не должна быть равна null");
    }

    @Test
    @DisplayName("Хеш-коды равных конфигураций одинаковы")
    void testHashCodeConsistency() {
        PasswordGenerationConfig config1 = new PasswordGenerationConfig(50);
        PasswordGenerationConfig config2 = new PasswordGenerationConfig(50);
        config1.setUseLatin(true);
        config1.addRequiredCharacter('A');
        config2.setUseLatin(true);
        config2.addRequiredCharacter('A');

        assertEquals(config1.hashCode(), config2.hashCode(),
                "Хеш-коды равных конфигураций должны совпадать");
    }

    @Test
    @DisplayName("toString возвращает не null строку")
    void testToString() {
        String str = config.toString();
        assertNotNull(str, "toString должен вернуть строку");
        assertTrue(str.contains("PasswordGenerationConfig"), "toString должен содержать имя класса");
    }
}
