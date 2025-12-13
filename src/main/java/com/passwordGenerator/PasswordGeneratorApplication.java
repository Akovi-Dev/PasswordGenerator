package com.passwordGenerator;

import com.passwordGenerator.ui.console.PasswordGeneratorConsoleUI;
import com.passwordGenerator.ui.gui.PasswordGeneratorGUI;
import com.passwordGenerator.utils.ArgumentParser;

import javafx.application.Application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * Главная точка входа приложения PasswordGenerator.
 * Это класс отвечает за инициализацию и запуск приложения.
 * Определяет, какой интерфейс использовать (консоль или GUI).
 * Если запуск GUI завершится ошибкой, автоматически переключается на консольный интерфейс.
 * Поддерживаемые аргументы:
 * {@code --ui=console} - запуск консольного интерфейса (по умолчанию)
 * {@code --ui=gui} - запуск графического интерфейса JavaFX
 *
 * @author Akovi
 * @see PasswordGeneratorConsoleUI
 * @see PasswordGeneratorGUI
 * @see ArgumentParser
 */
public class PasswordGeneratorApplication {

    private static final Logger logger = LogManager.getLogger(PasswordGeneratorApplication.class);

    /**
     * Запускает приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        try {
            logger.info("Запуск PasswordGenerator");
            logArguments(args);

            String uiMode = ArgumentParser.extractUiMode(args);
            logger.info("Определен тип интерфейса: {}", uiMode);

            if ("gui".equalsIgnoreCase(uiMode)) {
                logger.info("Инициализация GUI приложения...");
                launchGuiWithFallback();
            } else {
                logger.info("Инициализация консольного приложения...");
                launchConsole();
            }
        } catch (Exception e) {
            logger.error("КРИТИЧЕСКАЯ ОШИБКА ПРИ ЗАПУСКЕ ПРИЛОЖЕНИЯ", e);
            System.err.println("\nКРИТИЧЕСКАЯ ОШИБКА ПРИЛОЖЕНИЯ: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Логирует переданные аргументы командной строки.
     *
     * @param args аргументы командной строки
     */
    private static void logArguments(String[] args) {
        if (args != null && args.length > 0) {
            logger.debug("Переданы аргументы командной строки:");
            Arrays.stream(args).forEach(arg -> logger.debug(" arg: {}", arg));
        } else {
            logger.debug("Аргументы командной строки не переданы (по умолчанию консоль)");
        }
    }

    /**
     * Запускает консольный интерфейс приложения.
     * Создаёт новый объект {@link PasswordGeneratorConsoleUI} и вызывает его метод {@code run()},
     * который запускает интерактивный консольный интерфейс.
     *
     * @see PasswordGeneratorConsoleUI#run()
     */
    private static void launchConsole() {
        try {
            PasswordGeneratorConsoleUI consoleUI = new PasswordGeneratorConsoleUI();
            logger.info("Запуск консольного интерфейса...");
            consoleUI.run();
            logger.info("Консольный интерфейс завершил работу нормально");
        } catch (Exception e) {
            logger.error("Ошибка при запуске консольного интерфейса", e);
            System.err.println("\nОшибка при запуске консольного интерфейса: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Запускает графический интерфейс с автоматическим переключением на консоль при ошибке.
     *
     * @see PasswordGeneratorGUI
     * @see PasswordGeneratorGUI#start(javafx.stage.Stage)
     */
    private static void launchGuiWithFallback() {
        try {
            logger.info("Запуск JavaFX приложения...");
            Application.launch(PasswordGeneratorGUI.class);
            logger.info("Графический интерфейс завершил работу нормально");
        } catch (Exception e) {
            logger.warn("Ошибка при запуске графического интерфейса: {}", e.getMessage(), e);
            logger.info("Переключение на консольный интерфейс из-за ошибки: {}", e.getMessage());
            launchConsole();
        }
    }
}