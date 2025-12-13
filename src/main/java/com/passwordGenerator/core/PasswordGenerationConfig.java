package com.passwordGenerator.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Конфигурация для генерации пароля.
 * Хранит все параметры пароля: длину, типы символов и обязательные символы.
 *
 * @author Akovi
 */
public class PasswordGenerationConfig {

    private int length;

    private boolean useLatin;

    private boolean useCyrillic;

    private boolean useDigits;

    private boolean useSpecial;

    private final Set<Character> requiredCharacters;

    private static final int MAX_PASSWORD_LENGTH = 1_000_000;

    /**
     * Конструктор конфигурации с заданной длиной.
     *
     * @param length длина генерируемого пароля
     * @throws IllegalArgumentException если длина меньше или равна 0
     */
    public PasswordGenerationConfig(int length) {
        setLength(length);
        this.useLatin = false;
        this.useCyrillic = false;
        this.useDigits = false;
        this.useSpecial = false;
        this.requiredCharacters = new HashSet<>();
    }

    /**
     * Возвращает длину пароля.
     *
     * @return длина пароля
     */
    public int getLength() {
        return length;
    }

    /**
     * Устанавливает длину пароля с валидацией.
     *
     * @param length новая длина
     * @throws IllegalArgumentException если length меньше или равна 0
     */
    public void setLength(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException(
                    "Длина пароля должна быть положительной, получено: " + length);
        }
        this.length = length;
    }

    /**
     * Проверяет, используются ли латинские буквы.
     *
     * @return true если используются латинские буквы
     */
    public boolean isUseLatin() {
        return useLatin;
    }

    /**
     * Устанавливает использование латинских букв.
     *
     * @param useLatin true для использования
     */
    public void setUseLatin(boolean useLatin) {
        this.useLatin = useLatin;
    }

    /**
     * Проверяет, используются ли кириллические буквы.
     *
     * @return true если используются кириллические буквы
     */
    public boolean isUseCyrillic() {
        return useCyrillic;
    }

    /**
     * Устанавливает использование кириллических букв.
     *
     * @param useCyrillic true для использования
     */
    public void setUseCyrillic(boolean useCyrillic) {
        this.useCyrillic = useCyrillic;
    }

    /**
     * Проверяет, используются ли цифры.
     *
     * @return true если используются цифры
     */
    public boolean isUseDigits() {
        return useDigits;
    }

    /**
     * Устанавливает использование цифр.
     *
     * @param useDigits true для использования
     */
    public void setUseDigits(boolean useDigits) {
        this.useDigits = useDigits;
    }

    /**
     * Проверяет, используются ли специальные символы.
     *
     * @return true если используются специальные символы
     */
    public boolean isUseSpecial() {
        return useSpecial;
    }

    /**
     * Устанавливает использование специальных символов.
     *
     * @param useSpecial true для использования
     */
    public void setUseSpecial(boolean useSpecial) {
        this.useSpecial = useSpecial;
    }

    /**
     * Добавляет символ в набор требуемых символов.
     * Использует Set для предотвращения дубликатов.
     *
     * @param character символ для добавления
     * @throws IllegalArgumentException если количество требуемых символов больше длины пароля
     * @throws NullPointerException если character равен null
     */
    public void addRequiredCharacter(Character character) {
        Objects.requireNonNull(character, "Символ не может быть null");
        if (requiredCharacters.size() >= length) {
            throw new IllegalArgumentException(
                    "Количество обязательных символов не может быть >= длины пароля");
        }
        requiredCharacters.add(character);
    }

    /**
     * Возвращает неизменяемый набор требуемых символов.
     *
     * @return неизменяемый набор требуемых символов
     */
    public Set<Character> getRequiredCharacters() {
        return Collections.unmodifiableSet(requiredCharacters);
    }

    /**
     * Возвращает количество требуемых символов.
     *
     * @return количество символов в наборе требуемых
     */
    public int getRequiredCharacterCount() {
        return requiredCharacters.size();
    }

    /**
     * Возвращает максимально допустимую длину пароля.
     *
     * @return максимальная длина пароля
     */
    public static int getMaxPasswordLength() {
        return MAX_PASSWORD_LENGTH;
    }

    /**
     * Возвращает строковое представление конфигурации.
     *
     * @return строка, отображающая текущую конфигурацию
     */
    @Override
    public String toString() {
        return "PasswordGenerationConfig{" +
                "length=" + length +
                ", useLatin=" + useLatin +
                ", useCyrillic=" + useCyrillic +
                ", useDigits=" + useDigits +
                ", useSpecial=" + useSpecial +
                ", requiredCharacters=" + requiredCharacters +
                '}';
    }

    /**
     * Сравнивает две конфигурации на базе всех полей.
     *
     * @param o объект для сравнения
     * @return true если конфигурации полностью идентичны, false иначе
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordGenerationConfig that = (PasswordGenerationConfig) o;
        return length == that.length &&
                useLatin == that.useLatin &&
                useCyrillic == that.useCyrillic &&
                useDigits == that.useDigits &&
                useSpecial == that.useSpecial &&
                Objects.equals(requiredCharacters, that.requiredCharacters);
    }

    /**
     * Возвращает хеш-код конфигурации на основе всех полей.
     *
     * @return хеш-код объекта
     */
    @Override
    public int hashCode() {
        return Objects.hash(length, useLatin, useCyrillic, useDigits, useSpecial, requiredCharacters);
    }
}
