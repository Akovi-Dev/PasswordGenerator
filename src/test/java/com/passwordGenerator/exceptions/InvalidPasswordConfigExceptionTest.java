package com.passwordGenerator.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты для класса InvalidPasswordConfigException.
 * Тестируют создание и обработку исключений конфигурации паролей.
 *
 * @author Test Suite
 * @see InvalidPasswordConfigException
 */
@DisplayName("InvalidPasswordConfigException Unit Tests")
public class InvalidPasswordConfigExceptionTest {

    @Test
    @DisplayName("Создаёт исключение с сообщением об ошибке")
    void testExceptionWithMessage() {
        String message = "Ошибка конфигурации";
        InvalidPasswordConfigException exception =
                new InvalidPasswordConfigException(message);

        assertEquals(message, exception.getMessage(),
                "Сообщение об ошибке должно совпадать");
        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("Выбрасывает исключение и может быть поймано")
    void testExceptionCanBeThrown() {
        String message = "Конфигурация невалидна";

        assertThrows(InvalidPasswordConfigException.class,
                () -> {
                    throw new InvalidPasswordConfigException(message);
                },
                "Исключение должно быть выброшено");
    }

    @Test
    @DisplayName("Исключение наследует от Exception")
    void testExceptionInheritance() {
        InvalidPasswordConfigException exception =
                new InvalidPasswordConfigException("Test");

        assertInstanceOf(Exception.class, exception, "InvalidPasswordConfigException должно наследоваться от Exception");
    }

    @Test
    @DisplayName("Исключение содержит пустое сообщение")
    void testExceptionWithEmptyMessage() {
        InvalidPasswordConfigException exception =
                new InvalidPasswordConfigException("");

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().isEmpty());
    }

    @Test
    @DisplayName("toString возвращает информацию об исключении")
    void testExceptionToString() {
        InvalidPasswordConfigException exception =
                new InvalidPasswordConfigException("Тестовая ошибка");

        String str = exception.toString();
        assertNotNull(str);
        assertTrue(str.contains("InvalidPasswordConfigException"));
    }
}
