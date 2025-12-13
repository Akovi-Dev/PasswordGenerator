package com.passwordGenerator.utils;

import java.util.Arrays;

/**
 * Утилитный класс, отвечающий за извлечение и валидацию аргументов запуска приложения из массива String[].
 * Поддерживаемые аргументы:
 * {@code --ui=console} - запуск консольного интерфейса (по умолчанию)
 * {@code --ui=gui} - запуск графического интерфейса JavaFX
 *
 * @author Akovi
 */
public final class ArgumentParser {

    private static final String DEFAULT_UI_MODE = "console";
    private static final String UI_ARGUMENT_PREFIX = "--ui=";

    /**
     * Приватный конструктор для предотвращения создания экземпляров.
     * Этот класс предназначен только для использования его статических методов.
     */
    private ArgumentParser() {
        throw new AssertionError("Утилитный класс не должен быть инстанцирован");
    }

    /**
     * Извлекает режим UI из аргументов командной строки.
     * Ищет аргумент вида {@code --ui=X}, где X может быть "console" или "gui".
     *
     * @param args аргументы командной строки (может быть null)
     * @return "console" или "gui", или "console" по умолчанию если аргумент не найден
     */
    public static String extractUiMode(String[] args) {
        if (args == null) {
            return DEFAULT_UI_MODE;
        }

        return Arrays.stream(args)
                .filter(arg -> arg != null && arg.startsWith(UI_ARGUMENT_PREFIX))
                .map(arg -> arg.substring(UI_ARGUMENT_PREFIX.length()).trim().toLowerCase())
                .filter(mode -> mode.equals("gui") || mode.equals("console"))
                .findFirst()
                .orElse(DEFAULT_UI_MODE);
    }
}
