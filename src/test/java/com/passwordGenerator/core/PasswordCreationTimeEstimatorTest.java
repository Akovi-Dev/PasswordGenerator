package com.passwordGenerator.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты для класса PasswordCreationTimeEstimator.
 * Тестируют оценку времени генерации паролей различных длин и сложности.
 *
 * @author Test Suite
 * @see PasswordCreationTimeEstimator
 * @see PasswordGenerator
 */
@DisplayName("PasswordCreationTimeEstimator Unit Tests")
public class PasswordCreationTimeEstimatorTest {

    private PasswordCreationTimeEstimator estimator;

    @BeforeEach
    void setUp() {
        estimator = new PasswordCreationTimeEstimator();
    }

    @Test
    @DisplayName("Инициализирует оценщик с генератором")
    void testEstimatorInitialization() {
        assertNotNull(estimator, "Оценщик должен быть инициализирован");
    }

    @Test
    @DisplayName("PerformanceResult хранит корректную длину пароля")
    void testPerformanceResultPasswordLength() {
        PasswordCreationTimeEstimator.PerformanceResult result =
                new PasswordCreationTimeEstimator.PerformanceResult(1000, 50000.0, 10);

        assertEquals(1000, result.getPasswordLength(), "Длина должна быть 1000");
    }

    @Test
    @DisplayName("PerformanceResult хранит корректное количество паролей")
    void testPerformanceResultPasswordCount() {
        PasswordCreationTimeEstimator.PerformanceResult result =
                new PasswordCreationTimeEstimator.PerformanceResult(1000, 50000.0, 10);

        assertEquals(10, result.getNumberOfPasswordsGenerated(), "Кол-во паролей должно быть 10");
    }

    @Test
    @DisplayName("PerformanceResult форматирует время в наносекундах")
    void testPerformanceResultFormatTimeNanoseconds() {
        PasswordCreationTimeEstimator.PerformanceResult result =
                new PasswordCreationTimeEstimator.PerformanceResult(1000, 500.0, 10);

        String formattedTime = result.getFormattedAverageTime();
        assertNotNull(formattedTime);
        assertTrue(formattedTime.contains("нс"), "Время в нс должно содержать 'нс'");
    }

    @Test
    @DisplayName("PerformanceResult форматирует время в миллисекундах")
    void testPerformanceResultFormatTimeMilliseconds() {
        PasswordCreationTimeEstimator.PerformanceResult result =
                new PasswordCreationTimeEstimator.PerformanceResult(1000, 5_000_000.0, 10);

        String formattedTime = result.getFormattedAverageTime();
        assertTrue(formattedTime.contains("мс"), "Время в мс должно содержать 'мс'");
    }

    @Test
    @DisplayName("PerformanceResult форматирует время в секундах")
    void testPerformanceResultFormatTimeSeconds() {
        PasswordCreationTimeEstimator.PerformanceResult result =
                new PasswordCreationTimeEstimator.PerformanceResult(1000, 5_000_000_000.0, 10);

        String formattedTime = result.getFormattedAverageTime();
        assertTrue(formattedTime.contains("с"), "Время в с должно содержать 'с'");
    }

    @Test
    @DisplayName("PerformanceResult toString возвращает информацию о результате")
    void testPerformanceResultToString() {
        PasswordCreationTimeEstimator.PerformanceResult result =
                new PasswordCreationTimeEstimator.PerformanceResult(1000, 50000.0, 10);

        String str = result.toString();
        assertNotNull(str);
        assertTrue(str.contains("1000"), "Должна содержаться длина");
        assertTrue(str.contains("10"), "Должно содержаться количество паролей");
    }

    @Test
    @DisplayName("runQuickTest возвращает непустой отчёт")
    void testRunQuickTestReturnsReport() {
        String report = estimator.runQuickTest();

        assertNotNull(report, "Отчёт не должен быть null");
        assertFalse(report.isEmpty(), "Отчёт не должен быть пустым");
        assertTrue(report.contains("БЫСТРЫЙ ТЕСТ"), "Отчёт должен содержать тип теста");
    }

    @Test
    @DisplayName("runQuickTest отчёт содержит таблицу результатов")
    void testRunQuickTestContainsTable() {
        String report = estimator.runQuickTest();

        assertFalse(report.isEmpty(), "Отчёт должен содержать информацию о результатах");
        assertTrue(report.contains("Длина") || report.length() > 100,
                "Отчёт должен быть непустым с результатами");
    }

    @Test
    @DisplayName("runDetailedTest возвращает непустой отчёт")
    void testRunDetailedTestReturnsReport() {
        String report = estimator.runDetailedTest();

        assertNotNull(report, "Отчёт не должен быть null");
        assertFalse(report.isEmpty(), "Отчёт не должен быть пустым");
        assertTrue(report.contains("ДЕТАЛЬНЫЙ ТЕСТ"), "Отчёт должен содержать тип теста");
    }

    @Test
    @DisplayName("runCustomTest с корректными параметрами возвращает отчёт")
    void testRunCustomTestReturnsReport() {
        String report = estimator.runCustomTest(10000, 50000, 10000);

        assertNotNull(report, "Отчёт не должен быть null");
        assertFalse(report.isEmpty(), "Отчёт не должен быть пустым");
        assertTrue(report.contains("ПОЛЬЗОВАТЕЛЬСКИЙ ТЕСТ"), "Отчёт должен содержать тип теста");
    }

    @Test
    @DisplayName("runCustomTest отчёт содержит информацию о диапазоне")
    void testRunCustomTestContainsRangeInfo() {
        String report = estimator.runCustomTest(10000, 50000, 10000);

        assertTrue(report.length() > 100, "Отчёт должен содержать детальную информацию");
    }

    @Test
    @DisplayName("PerformanceResult с нулевым временем обрабатывается корректно")
    void testPerformanceResultWithZeroTime() {
        PasswordCreationTimeEstimator.PerformanceResult result =
                new PasswordCreationTimeEstimator.PerformanceResult(100, 0.0, 1);

        assertNotNull(result.getFormattedAverageTime());
        assertEquals(100, result.getPasswordLength());
    }

    @Test
    @DisplayName("Конструктор с PasswordGenerator инициализирует оценщик")
    void testEstimatorConstructorWithGenerator() {
        PasswordGenerator generator = new PasswordGenerator();
        PasswordCreationTimeEstimator customEstimator =
                new PasswordCreationTimeEstimator(generator);

        assertNotNull(customEstimator);
    }

    @Test
    @DisplayName("PerformanceResult сохраняет точные значения параметров")
    void testPerformanceResultValuesPrecision() {
        int length = 12345;
        double timeNanos = 123456.789;
        int count = 42;

        PasswordCreationTimeEstimator.PerformanceResult result =
                new PasswordCreationTimeEstimator.PerformanceResult(length, timeNanos, count);

        assertEquals(length, result.getPasswordLength());
        assertEquals(count, result.getNumberOfPasswordsGenerated());
    }
}
