package com.passwordGenerator.ui.console;

import com.passwordGenerator.core.PasswordCreationTimeEstimator;
import com.passwordGenerator.core.PasswordGenerationConfig;
import com.passwordGenerator.core.PasswordGenerator;
import com.passwordGenerator.exceptions.InvalidPasswordConfigException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * Консольный интерфейс приложения PasswordGenerator.
 * Предоставляет интерактивное меню для генерации паролей и тестирования производительности.
 *
 * @author Akovi
 * @see PasswordCreationTimeEstimator
 * @see PasswordGenerationConfig
 * @see PasswordGenerator
 * @see InvalidPasswordConfigException
 */
public class PasswordGeneratorConsoleUI {

    private static final Logger logger = LogManager.getLogger(PasswordGeneratorConsoleUI.class);
    private final Scanner scanner;
    private final Map<String, MenuCommand> commands;

    private static final String MENU_SEPARATOR = "=".repeat(70);
    private static final String APP_TITLE = "PasswordGenerator";

    /**
     * Внутренний класс для регистрации обработчиков команд.
     */
    private static class MenuCommand {

        private final String key;
        private final String description;
        private final Consumer<PasswordGeneratorConsoleUI> handler;

        /**
         * Конструктор команды меню.
         *
         * @param key код команды
         * @param description описание команды
         * @param handler обработчик команды
         */
        public MenuCommand(String key, String description,
                           Consumer<PasswordGeneratorConsoleUI> handler) {
            this.key = key;
            this.description = description;
            this.handler = handler;
        }
    }

    /**
     * Конструктор консольного интерфейса.
     * Инициализирует все доступные команды меню.
     */
    public PasswordGeneratorConsoleUI() {
        this.commands = new HashMap<>();
        this.scanner = new Scanner(System.in);
        registerCommands();
        logger.debug("ConsoleUI инициализирована с {} командами", commands.size());
    }

    /**
     * Регистрирует все доступные команды меню.
     */
    private void registerCommands() {
        commands.put("1", new MenuCommand("1",
                "Сгенерировать пароль с параметрами",
                this::handleGeneratePassword));
        commands.put("2", new MenuCommand("2",
                "Быстрый тест времени (10k, 100k, 1M)",
                this::handleQuickPerformanceTest));
        commands.put("3", new MenuCommand("3",
                "Детальный тест времени (10k-1M, шаг 100k)",
                this::handleDetailedPerformanceTest));
        commands.put("4", new MenuCommand("4",
                "Пользовательский тест",
                this::handleCustomPerformanceTest));
        commands.put("0", new MenuCommand("0",
                "Выход из приложения",
                this::handleExit));
    }

    /**
     * Запускает консольный интерфейс приложения.
     */
    public void run() {
        logger.info("Запуск консольного интерфейса приложения");
        try {
            boolean exit = false;
            while (!exit) {
                printMenu();
                if (!scanner.hasNextLine()) {
                    logger.warn("Входной поток закрыт, выход");
                    break;
                }

                String choice = scanner.nextLine().trim();
                exit = processUserChoice(choice);
            }
        } catch (Exception e) {
            logger.error("Критическая ошибка в консольном интерфейсе", e);
            System.err.println("Критическая ошибка приложения: " + e.getMessage());
        } finally {
            closeScanner();
            logger.info("Консольный интерфейс завершил работу");
        }
    }

    /**
     * Обрабатывает выбор пользователя из меню.
     *
     * @param choice выбранная команда
     * @return true если нужно выйти из приложения
     */
    private boolean processUserChoice(String choice) {
        return commands.values().stream()
                .filter(cmd -> cmd.key.equals(choice))
                .findFirst()
                .map(this::executeCommand)
                .orElseGet(() -> {
                    logger.warn("Неизвестная команда: {}", choice);
                    System.out.println("Неизвестный пункт меню. Пожалуйста, выберите от 0 до 4.");
                    return false;
                });
    }

    /**
     * Выполняет команду с обработкой исключений.
     *
     * @param command команда для выполнения
     * @return true если это команда выхода
     */
    private boolean executeCommand(MenuCommand command) {
        try {
            logger.debug("Выполнение команды: {}", command.key);
            command.handler.accept(this);
            return command.key.equals("0");
        } catch (Exception e) {
            logger.error("Ошибка при выполнении команды {}", command.key, e);
            System.out.println("Ошибка: " + e.getMessage());
            return false;
        }
    }

    /**
     * Закрывает сканер входных данных.
     */
    private void closeScanner() {
        try {
            if (scanner != null) {
                scanner.close();
                logger.debug("Scanner закрыт");
            }
        } catch (Exception e) {
            logger.warn("Ошибка при закрытии Scanner", e);
        }
    }

    /**
     * Выводит меню приложения в консоль.
     */
    private void printMenu() {
        System.out.println("\n" + MENU_SEPARATOR);
        System.out.println(APP_TITLE);
        System.out.println(MENU_SEPARATOR);

        commands.values().stream()
                .filter(cmd -> !cmd.key.equals("0"))
                .sorted(Comparator.comparingInt(a -> Integer.parseInt(a.key)))
                .forEach(cmd -> System.out.println(cmd.key + " - " + cmd.description));

        commands.values().stream()
                .filter(cmd -> cmd.key.equals("0"))
                .forEach(cmd -> System.out.println(cmd.key + " - " + cmd.description));

        System.out.println(MENU_SEPARATOR);
        System.out.print("Выберите действие (0-4): ");
    }

    /**
     * Обрабатывает генерацию пароля.
     */
    private void handleGeneratePassword(PasswordGeneratorConsoleUI passwordGeneratorConsoleUI) {
        System.out.println("\n" + MENU_SEPARATOR);
        System.out.println("ГЕНЕРАЦИЯ ПАРОЛЯ");
        System.out.println(MENU_SEPARATOR);

        int length = readPositiveInteger("Введите длину пароля (макс. 1000000): ", "Длина");
        if (length <= 0) return;

        PasswordGenerationConfig config = new PasswordGenerationConfig(length);

        if (readYesNo("\nИспользовать латиницу (a-z, A-Z)?")) {
            config.setUseLatin(true);
        }
        if (readYesNo("Использовать кириллицу (а-я, А-Я)?")) {
            config.setUseCyrillic(true);
        }
        if (readYesNo("Использовать цифры (0-9)?")) {
            config.setUseDigits(true);
        }
        if (readYesNo("Использовать спецсимволы (!@#$%^&*)?")) {
            config.setUseSpecial(true);
        }

        System.out.print("\nВведите обязательные символы (оставьте пусто, если нет): ");
        String requiredChars = scanner.nextLine();
        for (char c : requiredChars.toCharArray()) {
            try {
                config.addRequiredCharacter(c);
            } catch (IllegalArgumentException e) {
                logger.warn("Ошибка при добавлении обязательного символа", e);
                System.out.println("Не удалось добавить символ: " + e.getMessage());
                return;
            }
        }

        try {
            PasswordGenerator generator = new PasswordGenerator();
            String password = generator.generate(config);
            System.out.println("\n" + MENU_SEPARATOR);
            System.out.println("Сгенерированный пароль:");
            System.out.println(password);
            System.out.println(MENU_SEPARATOR);
            logger.info("Пароль успешно сгенерирован в консоли");
        } catch (InvalidPasswordConfigException e) {
            logger.warn("Ошибка конфигурации при генерации пароля", e);
            System.out.println("Ошибка конфигурации: " + e.getMessage());
        }
    }

    /**
     * Обрабатывает быстрый тест производительности.
     */
    private void handleQuickPerformanceTest(PasswordGeneratorConsoleUI passwordGeneratorConsoleUI) {
        System.out.println("\n" + MENU_SEPARATOR);
        System.out.println("БЫСТРЫЙ ТЕСТ ПРОИЗВОДИТЕЛЬНОСТИ");
        System.out.println("Тестирование 3 контрольных точек (10k, 100k, 1M)...\n");

        try {
            PasswordCreationTimeEstimator estimator = new PasswordCreationTimeEstimator();
            String report = estimator.runQuickTest();
            System.out.println(report);
            logger.info("Быстрый тест завершён успешно");
        } catch (Exception e) {
            logger.error("Ошибка при выполнении быстрого теста", e);
            System.out.println("Ошибка при выполнении теста: " + e.getMessage());
        }
    }

    /**
     * Обрабатывает детальный тест производительности.
     */
    private void handleDetailedPerformanceTest(PasswordGeneratorConsoleUI passwordGeneratorConsoleUI) {
        System.out.println("\n" + MENU_SEPARATOR);
        System.out.println("ДЕТАЛЬНЫЙ ТЕСТ ПРОИЗВОДИТЕЛЬНОСТИ");
        System.out.println("Тестирование диапазона 10k-1M (шаг 100k)...\n");

        try {
            PasswordCreationTimeEstimator estimator = new PasswordCreationTimeEstimator();
            String report = estimator.runDetailedTest();
            System.out.println(report);
            logger.info("Детальный тест завершён успешно");
        } catch (Exception e) {
            logger.error("Ошибка при выполнении детального теста", e);
            System.out.println("Ошибка при выполнении теста: " + e.getMessage());
        }
    }

    /**
     * Обрабатывает пользовательский тест производительности.
     */
    private void handleCustomPerformanceTest(PasswordGeneratorConsoleUI passwordGeneratorConsoleUI) {
        System.out.println("\n" + MENU_SEPARATOR);
        System.out.println("ПОЛЬЗОВАТЕЛЬСКИЙ ТЕСТ ПРОИЗВОДИТЕЛЬНОСТИ");
        System.out.println(MENU_SEPARATOR);

        int minLength = readPositiveInteger("Минимальная длина (от 10000): ", "Минимальная длина");
        if (minLength <= 0) return;

        int maxLength = readPositiveInteger("Максимальная длина (до 1000000): ", "Максимальная длина");
        if (maxLength <= 0) return;

        int step = readPositiveInteger("Шаг между расчётами: ", "Шаг");
        if (step <= 0) return;

        if (minLength > maxLength) {
            logger.warn("Минимальная длина больше максимальной: {} > {}", minLength, maxLength);
            System.out.println("Ошибка: минимальная длина не должна быть больше максимальной");
            return;
        }

        System.out.println("\nДиапазон: " + minLength + " - " + maxLength + ", шаг: " + step + "\n");

        try {
            logger.info("Запуск пользовательского теста: {} - {}, шаг {}", minLength, maxLength, step);
            PasswordCreationTimeEstimator estimator = new PasswordCreationTimeEstimator();
            String report = estimator.runCustomTest(minLength, maxLength, step);
            System.out.println(report);
            logger.info("Пользовательский тест завершён успешно");
        } catch (Exception e) {
            logger.error("Ошибка при выполнении пользовательского теста", e);
            System.out.println("Ошибка при выполнении теста: " + e.getMessage());
        }
    }

    /**
     * Обрабатывает выход из приложения.
     */
    private void handleExit(PasswordGeneratorConsoleUI passwordGeneratorConsoleUI) {
        System.out.println("\nТут могла быть ваша реклама ;P");
        logger.info("Выход из приложения по команде пользователя");
    }

    /**
     * Читает положительное целое число из консоли.
     *
     * @param prompt сообщение для пользователя
     * @param fieldName название поля для сообщений об ошибке
     * @return прочитанное число или -1 при ошибке
     */
    private int readPositiveInteger(String prompt, String fieldName) {
        System.out.print(prompt);
        try {
            String input = scanner.nextLine().trim();
            int value = Integer.parseInt(input);
            if (value <= 0) {
                System.out.println("Ошибка: " + fieldName + " должен быть положительным!");
                logger.warn("Введено неположительное значение для {}: {}", fieldName, value);
                return -1;
            }
            return value;
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: пожалуйста, введите корректное число!");
            logger.warn("Ошибка при чтении {}: некорректный формат", fieldName);
            return -1;
        }
    }

    /**
     * Читает ответ "да/нет" из консоли.
     *
     * @param prompt вопрос для пользователя
     * @return true если ответ "да" (y/Y или д/Д), false иначе
     */
    private boolean readYesNo(String prompt) {
        System.out.print(prompt + " (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();
        boolean result = response.equals("y") || response.equals("yes") ||
                response.equals("д") || response.equals("да");
        logger.debug("Ответ на вопрос '{}': {}", prompt, result);
        return result;
    }
}
