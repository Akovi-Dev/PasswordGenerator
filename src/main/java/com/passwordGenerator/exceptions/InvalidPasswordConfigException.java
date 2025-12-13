package com.passwordGenerator.exceptions;

/**
 * Исключение, выбрасываемое при некорректной конфигурации генерации пароля.
 *
 * @author Akovi
 */
public class InvalidPasswordConfigException extends Exception {

    /**
     * Конструктор с сообщением об ошибке.
     *
     * @param message описание ошибки
     */
    public InvalidPasswordConfigException(String message) {
        super(message);
    }
}
