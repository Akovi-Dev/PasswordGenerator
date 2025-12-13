package com.passwordGenerator.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Класс для оценки времени создания паролей разной длины и сложности.
 * Выполняет бенчмарки для различных конфигураций параметров генерации.
 *
 * @author Akovi
 * @see PasswordGenerator
 * @see PasswordGenerationConfig
 */
public class PasswordCreationTimeEstimator {

    private static final Logger logger = LogManager.getLogger(PasswordCreationTimeEstimator.class);
    private final PasswordGenerator generator;

    private static final int DEFAULT_MIN_LENGTH = 10_000;
    private static final int DEFAULT_MAX_LENGTH = 1_000_000;
    private static final int PASSWORDS_PER_LENGTH = 10;

    /**
     * Внутренний класс для хранения результатов одного теста.
     * Содержит информацию о времени генерации пароля определённой длины.
     */
    public static class PerformanceResult {

        private final int passwordLength;
        private final double averageTimeNanos;
        private final int numberOfPasswordsGenerated;

        /**
         * Конструктор результата теста.
         *
         * @param passwordLength длина тестируемого пароля
         * @param averageTimeNanos среднее время генерации в наносекундах
         * @param numberOfPasswordsGenerated количество сгенерированных паролей
         */
        public PerformanceResult(int passwordLength, double averageTimeNanos,
                                 int numberOfPasswordsGenerated) {
            this.passwordLength = passwordLength;
            this.averageTimeNanos = averageTimeNanos;
            this.numberOfPasswordsGenerated = numberOfPasswordsGenerated;
        }

        /**
         * Возвращает длину пароля, использованную в тесте.
         *
         * @return целочисленное значение длины пароля
         */
        public int getPasswordLength() {
            return passwordLength;
        }

        /**
         * Возвращает количество паролей, сгенерированных для замера.
         *
         * @return целое число паролей
         */
        public int getNumberOfPasswordsGenerated() {
            return numberOfPasswordsGenerated;
        }

        /**
         * Форматирует среднее время в удобочитаемый вид.
         * Преобразует наносекунды в наиболее подходящую единицу:
         * - Наносекунды (нс) - если менее 1 миллисекунды
         * - Миллисекунды (мс) - если менее 1 секунды
         * - Секунды (с) - если более 1 секунды
         *
         * @return строка с временем в наиболее подходящей единице
         */
        public String getFormattedAverageTime() {
            double timeMs = averageTimeNanos / 1_000_000.0;
            if (timeMs < 1.0) {
                return String.format("%.2f нс", averageTimeNanos);
            } else if (timeMs < 1000.0) {
                return String.format("%.2f мс", timeMs);
            } else {
                return String.format("%.2f с", timeMs / 1000.0);
            }
        }

        /**
         * Возвращает строковое представление результата теста.
         * Формат: "Длина: X, Ср. время: Y, Кол-во: Z"
         *
         * @return строка с основными параметрами результата
         */
        @Override
        public String toString() {
            return String.format("Длина: %d, Ср. время: %s, Кол-во: %d",
                    getPasswordLength(), getFormattedAverageTime(),
                    getNumberOfPasswordsGenerated());
        }
    }

    /**
     * Конструктор оценщика времени создания паролей.
     *
     * @param generator экземпляр генератора паролей для тестирования
     */
    public PasswordCreationTimeEstimator(PasswordGenerator generator) {
        this.generator = generator;
        logger.info("Инициализирован PasswordCreationTimeEstimator");
    }

    /**
     * Конструктор по умолчанию с автоматической инициализацией генератора.
     * Создаёт новый экземпляр PasswordGenerator автоматически.
     */
    public PasswordCreationTimeEstimator() {
        this.generator = new PasswordGenerator();
        logger.info("Инициализирован PasswordCreationTimeEstimator с генератором по умолчанию");
    }

    /**
     * Измеряет среднее время генерации одного пароля определённой длины.
     *
     * @param passwordLength длина генерируемых паролей
     * @param numberOfPasswords количество паролей для генерации
     * @return результат измерения с временем
     * @throws RuntimeException если возникла ошибка при генерации
     */
    private PerformanceResult measureTime(int passwordLength, int numberOfPasswords) {
        logger.info("Начало измерения времени для длины: {}, паролей: {}",
                passwordLength, numberOfPasswords);
        PasswordGenerationConfig config = createDefaultConfig(passwordLength);
        long startTime = System.nanoTime();

        try {
            IntStream.range(0, numberOfPasswords)
                    .peek(i -> logProgress(i, numberOfPasswords, passwordLength))
                    .forEach(i -> generatePasswordSafely(config));
        } catch (Exception e) {
            logger.error("Ошибка при генерации паролей для длины {}: {}",
                    passwordLength, e.getMessage(), e);
            throw new RuntimeException("Ошибка измерения для длины " + passwordLength, e);
        }

        long endTime = System.nanoTime();
        long totalTimeNanos = endTime - startTime;
        double averageTimeNanos = (double) totalTimeNanos / numberOfPasswords;

        logger.info("Завершено для длины: {}. Среднее: {} нс", passwordLength, averageTimeNanos);
        return new PerformanceResult(passwordLength, averageTimeNanos, numberOfPasswords);
    }

    /**
     * Логирует прогресс выполнения тестирования.
     *
     * @param currentIndex текущий индекс
     * @param total общее количество
     * @param passwordLength длина пароля
     */
    private void logProgress(int currentIndex, int total, int passwordLength) {
        if (currentIndex % Math.max(1, total / 10) == 0 && currentIndex > 0) {
            logger.debug("Прогресс: {}/{} для длины {}", currentIndex, total, passwordLength);
        }
    }

    /**
     * Генерирует пароль с обработкой исключений.
     *
     * @param config конфигурация генерации
     */
    private void generatePasswordSafely(PasswordGenerationConfig config) {
        try {
            generator.generate(config);
        } catch (Exception e) {
            logger.error("Ошибка при генерации пароля в тесте", e);
            throw new RuntimeException("Ошибка генерации пароля", e);
        }
    }

    /**
     * Создает конфигурацию пароля по умолчанию для тестирования.
     *
     * @param passwordLength длина пароля
     * @return конфигурация с установленными параметрами
     */
    private PasswordGenerationConfig createDefaultConfig(int passwordLength) {
        PasswordGenerationConfig config = new PasswordGenerationConfig(passwordLength);
        config.setUseLatin(true);
        config.setUseDigits(true);
        config.setUseSpecial(true);
        return config;
    }

    /**
     * Выполняет тесты для списка длин пароля.
     *
     * @param lengthsToTest список длин для тестирования
     * @param passwordsPerLength количество паролей на каждую длину
     * @return список результатов измерений
     */
    private List<PerformanceResult> runPerformanceTest(List<Integer> lengthsToTest,
                                                       int passwordsPerLength) {
        logger.info("Начало тестирования для {} длин", lengthsToTest.size());
        List<PerformanceResult> results = lengthsToTest.stream()
                .map(length -> measureTime(length, passwordsPerLength))
                .peek(result -> logger.info("Результат: {}", result))
                .collect(Collectors.toList());
        logger.info("Завершено {} тестов", results.size());
        return results;
    }

    /**
     * Выполняет тесты для диапазона длин пароля с заданным шагом.
     *
     * @param minLength минимальная длина пароля
     * @param maxLength максимальная длина пароля
     * @param step шаг между измерениями
     * @param passwordsPerLength количество паролей на каждую длину
     * @return список результатов для всех длин в диапазоне
     */
    private List<PerformanceResult> runPerformanceTestRange(int minLength, int maxLength,
                                                            int step, int passwordsPerLength) {
        logger.info("Начало теста диапазона: {} - {}, шаг: {}", minLength, maxLength, step);
        List<Integer> lengthsToTest = IntStream.rangeClosed(minLength, maxLength)
                .filter(length -> (length - minLength) % step == 0)
                .boxed()
                .collect(Collectors.toList());
        logger.info("Будет протестировано {} длин", lengthsToTest.size());
        return runPerformanceTest(lengthsToTest, passwordsPerLength);
    }

    /**
     * Генерирует отчёт о результатах тестирования.
     * Форматирует результаты в таблицу с заголовком и разделителями.
     *
     * @param results список результатов измерений
     * @param testName название типа теста
     * @return отчёт в текстовом формате
     */
    private String generateReport(List<PerformanceResult> results, String testName) {
        StringBuilder report = new StringBuilder();
        report.append("\nОТЧЕТ О ТЕСТЕ ПРОИЗВОДИТЕЛЬНОСТИ ГЕНЕРАЦИИ ПАРОЛЕЙ\n");
        report.append(String.format("%-53s\n", testName));

        if (results == null || results.isEmpty()) {
            report.append("Нет данных для отчета.\n");
            return report.toString();
        }

        report.append(String.format("%-15s | %-20s | %-15s\n", "Длина", "Ср. время/пароль", "Кол-во"));
        report.append("─".repeat(55)).append("\n");

        for (PerformanceResult result : results) {
            report.append(String.format("%-15d | %-20s | %-15d\n",
                    result.getPasswordLength(),
                    result.getFormattedAverageTime(),
                    result.getNumberOfPasswordsGenerated()));
        }

        report.append("─".repeat(55)).append("\n");
        return report.toString();
    }

    /**
     * Тестирует 3 контрольные точки: 10k, 100k, 1M символов.
     *
     * @return отчёт о результатах в текстовом формате
     */
    public String runQuickTest() {
        logger.info("Запуск быстрого теста");
        List<Integer> lengthsToTest = List.of(10_000, 100_000, 1_000_000);
        List<PerformanceResult> results = runPerformanceTest(lengthsToTest, PASSWORDS_PER_LENGTH);
        return generateReport(results, "БЫСТРЫЙ ТЕСТ (3 контрольные точки)");
    }

    /**
     * Тестирует полный диапазон от 10k до 1M с шагом 100k.
     *
     * @return отчёт о результатах в текстовом формате
     */
    public String runDetailedTest() {
        logger.info("Запуск детального теста");
        List<PerformanceResult> results = runPerformanceTestRange(
                DEFAULT_MIN_LENGTH,
                DEFAULT_MAX_LENGTH,
                100_000,
                PASSWORDS_PER_LENGTH
        );
        return generateReport(results, "ДЕТАЛЬНЫЙ ТЕСТ (10k-1M, шаг 100k)");
    }

    /**
     * Тестирует пользовательский диапазон с пользовательским шагом.
     *
     * @param minLength минимальная длина
     * @param maxLength максимальная длина
     * @param step шаг теста
     * @return отчёт о результатах в текстовом формате
     */
    public String runCustomTest(int minLength, int maxLength, int step) {
        logger.info("Запуск пользовательского теста: {} - {}, шаг {}", minLength, maxLength, step);
        List<PerformanceResult> results = runPerformanceTestRange(
                minLength,
                maxLength,
                step,
                PASSWORDS_PER_LENGTH
        );
        return generateReport(results, String.format("ПОЛЬЗОВАТЕЛЬСКИЙ ТЕСТ (%d-%d, шаг %d)",
                minLength, maxLength, step));
    }
}
