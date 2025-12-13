package com.passwordGenerator.core;

import com.passwordGenerator.exceptions.InvalidPasswordConfigException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Генератор случайных паролей с заданными условиями.
 * Использует SecureRandom для криптографически надёжной генерации.
 *
 * @author Akovi
 * @see PasswordGenerationConfig
 * @see InvalidPasswordConfigException
 */
public class PasswordGenerator {

    private static final Logger logger = LogManager.getLogger(PasswordGenerator.class);
    private final SecureRandom random = new SecureRandom();

    /**
     * Перечисление наборов символов для генерации пароля.
     * Каждый набор содержит строку уникальных символов определённого типа.
     * Используется для построения алфавита при генерации паролей.
     */
    private enum CharacterSet {
        /** Строчные латинские буквы a-z */
        LATIN_LOWER("abcdefghijklmnopqrstuvwxyz"),
        /** Заглавные латинские буквы A-Z */
        LATIN_UPPER("ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
        /** Строчные кириллические буквы (русский алфавит в нижнем регистре) */
        CYRILLIC_LOWER("абвгдежзийклмнопрстуфхцчшщъыьэюя"),
        /** Заглавные кириллические буквы (русский алфавит в верхнем регистре) */
        CYRILLIC_UPPER("АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ"),
        /** Цифровые символы 0-9 */
        DIGITS("0123456789"),
        /** Специальные символы для повышения сложности пароля */
        SPECIAL("!@#$%^&*()_+-=[]{}|;:,.<>?");

        /** Строка с символами этого набора */
        private final String characters;

        /**
         * Конструктор для инициализации набора символов.
         *
         * @param characters строка уникальных символов для этого набора
         */
        CharacterSet(String characters) {
            this.characters = characters;
        }

        /**
         * Возвращает строку с символами этого набора.
         *
         * @return строка символов, не может быть null
         */
        public String getCharacters() {
            return characters;
        }
    }

    /**
     * Генерирует пароль по заданной конфигурации.
     *
     * @param config конфигурация с параметрами генерации
     * @return сгенерированный пароль нужной длины со всеми обязательными символами
     * @throws InvalidPasswordConfigException если конфигурация некорректна
     */
    public String generate(PasswordGenerationConfig config) throws InvalidPasswordConfigException {
        validateConfig(config);
        List<Character> alphabet = buildAlphabet(config);
        List<Character> passwordChars = new ArrayList<>(config.getRequiredCharacters());

        logger.debug("Добавлены обязательные символы: {}", passwordChars);

        int remainingLength = config.getLength() - passwordChars.size();
        List<Character> randomChars = IntStream.range(0, remainingLength)
                .mapToObj(i -> alphabet.get(random.nextInt(alphabet.size())))
                .toList();

        passwordChars.addAll(randomChars);
        Collections.shuffle(passwordChars, random);

        String password = passwordChars.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());

        logger.info("Пароль успешно сгенерирован. Длина: {}", password.length());
        return password;
    }

    /**
     * Создает алфавит из выбранных типов символов.
     *
     * @param config конфигурация генерации
     * @return список всех доступных символов для пароля
     */
    private List<Character> buildAlphabet(PasswordGenerationConfig config) {
        String alphabet = Stream.of(
                        config.isUseLatin() ? (CharacterSet.LATIN_LOWER.getCharacters() +
                                CharacterSet.LATIN_UPPER.getCharacters()) : "",
                        config.isUseCyrillic() ? (CharacterSet.CYRILLIC_LOWER.getCharacters() +
                                CharacterSet.CYRILLIC_UPPER.getCharacters()) : "",
                        config.isUseDigits() ? CharacterSet.DIGITS.getCharacters() : "",
                        config.isUseSpecial() ? CharacterSet.SPECIAL.getCharacters() : ""
                )
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining());

        return alphabet.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
    }

    /**
     * Валидирует конфигурацию перед генерацией пароля.
     *
     * @param config конфигурация для проверки
     * @throws InvalidPasswordConfigException если конфигурация некорректна
     */
    private void validateConfig(PasswordGenerationConfig config) throws InvalidPasswordConfigException {
        if (config.getLength() <= 0) {
            String msg = "Длина пароля должна быть положительной, получено: " + config.getLength();
            logger.error(msg);
            throw new InvalidPasswordConfigException(msg);
        }

        if (config.getLength() > PasswordGenerationConfig.getMaxPasswordLength()) {
            String msg = "Слишком большая длина пароля! Возьмите число меньше " +
                    PasswordGenerationConfig.getMaxPasswordLength();
            logger.error(msg);
            throw new InvalidPasswordConfigException(msg);
        }

        int requiredCount = config.getRequiredCharacterCount();
        if (requiredCount > config.getLength()) {
            String msg = String.format("Обязательных символов (%d) больше чем длина пароля (%d)",
                    requiredCount, config.getLength());
            logger.error(msg);
            throw new InvalidPasswordConfigException(msg);
        }

        boolean hasAnyType = Stream.of(
                config.isUseLatin(),
                config.isUseCyrillic(),
                config.isUseDigits(),
                config.isUseSpecial()
        ).anyMatch(b -> b);

        if (!hasAnyType) {
            String msg = "Должен быть выбран хотя бы один тип символов (латиница, кириллица, цифры или спецсимволы)";
            logger.error(msg);
            throw new InvalidPasswordConfigException(msg);
        }

        logger.debug("Конфигурация валидна: длина={}, обязательные={}",
                config.getLength(), requiredCount);
    }
}
