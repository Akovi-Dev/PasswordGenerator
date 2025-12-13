package com.passwordGenerator.ui.gui;

import com.passwordGenerator.core.PasswordCreationTimeEstimator;
import com.passwordGenerator.core.PasswordGenerationConfig;
import com.passwordGenerator.core.PasswordGenerator;
import com.passwordGenerator.exceptions.InvalidPasswordConfigException;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Графический интерфейс приложения PasswordGenerator на JavaFX.
 * Позволяет генерировать пароли с заданными параметрами и проводить расчёты времени создания.
 *
 * @author Akovi
 * @see PasswordCreationTimeEstimator
 * @see PasswordGenerationConfig
 * @see PasswordGenerator
 * @see InvalidPasswordConfigException
 */
public class PasswordGeneratorGUI extends Application {

    private static final Logger logger = LogManager.getLogger(PasswordGeneratorGUI.class);

    private final PasswordGenerator generator = new PasswordGenerator();
    private final PasswordCreationTimeEstimator estimator = new PasswordCreationTimeEstimator(generator);

    // UI компоненты - вкладка Генерация
    private TextField generateLengthField;
    private CheckBox generateLatinBox;
    private CheckBox generateCyrillicBox;
    private CheckBox generateDigitsBox;
    private CheckBox generateSpecialBox;
    private TextField generateRequiredField;
    private TextArea generateResultArea;

    // UI компоненты - вкладка Расчёты
    private TextArea estimateResultArea;
    private Button quickTestButton;
    private Button detailedTestButton;
    private Button customTestButton;
    private Label estimateStatusLabel;

    /**
     * Инициализирует главное окно приложения с двумя вкладками.
     * Точка входа для JavaFX приложения.
     *
     * @param stage главное окно приложения
     */
    @Override
    public void start(Stage stage) {
        logger.info("Запуск GUI приложения PasswordGenerator");
        try {
            TabPane tabPane = new TabPane();
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

            Tab generateTab = createGenerateTab();
            Tab estimateTab = createEstimateTab();

            tabPane.getTabs().addAll(generateTab, estimateTab);

            Scene scene = new Scene(tabPane, 900, 700);
            stage.setTitle("PasswordGenerator");
            stage.setScene(scene);
            stage.show();

            logger.info("GUI приложение успешно запущено");

        } catch (Exception e) {
            logger.error("Критическая ошибка при инициализации GUI", e);
            showAlert(Alert.AlertType.ERROR, "Критическая ошибка",
                    "Не удалось инициализировать приложение");
            System.exit(1);
        }
    }

    /**
     * Создает вкладку "Генерация пароля".
     *
     * @return Tab с интерфейсом генерации
     */
    private Tab createGenerateTab() {
        logger.debug("Создание вкладки 'Генерация'");
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        root.getChildren().addAll(
                createLengthSection(),
                createCharacterTypesSection(),
                createRequiredCharactersSection(),
                createGenerateButton(),
                createResultSection()
        );

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);

        Tab tab = new Tab("Генерация", scrollPane);
        tab.setClosable(false);

        logger.debug("Вкладка 'Генерация' создана успешно");
        return tab;
    }

    /**
     * Создает вкладку "Расчёт времени создания".
     *
     * @return Tab с интерфейсом расчётов
     */
    private Tab createEstimateTab() {
        logger.debug("Создание вкладки 'Расчёты'");
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label headerLabel = new Label("Расчёт времени создания паролей");
        headerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        root.getChildren().add(headerLabel);

        root.getChildren().add(createTestButtonsSection());
        root.getChildren().add(new Separator());
        root.getChildren().add(createTestResultSection());

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);

        Tab tab = new Tab("Расчёты", scrollPane);
        tab.setClosable(false);

        logger.debug("Вкладка 'Расчёты' создана успешно");
        return tab;
    }

    /**
     * Создает секцию для ввода длины пароля.
     *
     * @return HBox с полем ввода длины
     */
    private HBox createLengthSection() {
        HBox box = new HBox(10);
        applyBorderStyle(box);
        box.setPadding(new Insets(10));

        Label label = new Label("Длина пароля:");
        label.setStyle("-fx-font-weight: bold;");

        generateLengthField = new TextField();
        generateLengthField.setPromptText("<= 1 000 000");
        generateLengthField.setPrefWidth(150);

        box.getChildren().addAll(label, generateLengthField);

        logger.debug("Секция 'Длина пароля' создана");
        return box;
    }

    /**
     * Создает секцию с выбором типов символов.
     *
     * @return VBox с чекбоксами типов символов
     */
    private VBox createCharacterTypesSection() {
        VBox box = new VBox(8);
        applyBorderStyle(box);
        box.setPadding(new Insets(10));

        Label label = new Label("Типы символов:");
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");

        generateLatinBox = new CheckBox("Латиница (a-z, A-Z)");
        generateCyrillicBox = new CheckBox("Кириллица (а-я, А-Я)");
        generateDigitsBox = new CheckBox("Цифры (0-9)");
        generateSpecialBox = new CheckBox("Спецсимволы (!@#$%^&*)");

        box.getChildren().addAll(label, generateLatinBox, generateCyrillicBox,
                generateDigitsBox, generateSpecialBox);

        logger.debug("Секция 'Типы символов' создана");
        return box;
    }

    /**
     * Создает секцию для ввода обязательных символов.
     *
     * @return HBox с полем ввода обязательных символов
     */
    private HBox createRequiredCharactersSection() {
        HBox box = new HBox(10);
        applyBorderStyle(box);
        box.setPadding(new Insets(10));

        Label label = new Label("Обязательные символы:");
        label.setStyle("-fx-font-weight: bold;");

        generateRequiredField = new TextField();
        generateRequiredField.setPrefWidth(200);

        box.getChildren().addAll(label, generateRequiredField);

        logger.debug("Секция 'Обязательные символы' создана");
        return box;
    }

    /**
     * Создает кнопку генерации пароля.
     *
     * @return Button для запуска генерации
     */
    private Button createGenerateButton() {
        Button button = new Button("Сгенерировать пароль");
        button.setPrefWidth(200);
        button.setStyle("-fx-font-size: 14; -fx-padding: 10; " +
                "-fx-background-color: #4CAF50; -fx-text-fill: white;");
        button.setOnAction(e -> handleGeneratePassword());

        logger.debug("Кнопка 'Сгенерировать пароль' создана");
        return button;
    }

    /**
     * Создает секцию результата генерации.
     *
     * @return VBox с полем результата
     */
    private VBox createResultSection() {
        VBox box = new VBox(10);

        Label label = new Label("Результат:");
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");

        generateResultArea = new TextArea();
        generateResultArea.setWrapText(true);
        generateResultArea.setPrefHeight(200);
        generateResultArea.setEditable(false);
        generateResultArea.setStyle("-fx-control-inner-background: #f5f5f5; " +
                "-fx-font-family: 'Courier New';");

        box.getChildren().addAll(label, generateResultArea);

        logger.debug("Секция 'Результат' создана");
        return box;
    }

    /**
     * Создает секцию с кнопками тестов.
     *
     * @return HBox с кнопками запуска различных тестов
     */
    private HBox createTestButtonsSection() {
        HBox box = new HBox(10);
        box.setPadding(new Insets(10));

        quickTestButton = new Button("Быстрый тест\n(10k, 100k, 1M)");
        quickTestButton.setPrefWidth(150);
        quickTestButton.setStyle("-fx-font-size: 11; -fx-padding: 10; " +
                "-fx-background-color: #FF9800; -fx-text-fill: white; -fx-wrap-text: true;");
        quickTestButton.setOnAction(e -> handleQuickTestAsync());

        detailedTestButton = new Button("Детальный тест\n(10k-1M, шаг 100k)");
        detailedTestButton.setPrefWidth(150);
        detailedTestButton.setStyle("-fx-font-size: 11; -fx-padding: 10; " +
                "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-wrap-text: true;");
        detailedTestButton.setOnAction(e -> handleDetailedTestAsync());

        customTestButton = new Button("Пользовательский тест\n(Ваши параметры)");
        customTestButton.setPrefWidth(150);
        customTestButton.setStyle("-fx-font-size: 11; -fx-padding: 10; " +
                "-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-wrap-text: true;");
        customTestButton.setOnAction(e -> handleCustomTestAsync());

        box.getChildren().addAll(quickTestButton, detailedTestButton, customTestButton);

        logger.debug("Секция кнопок тестов создана");
        return box;
    }

    /**
     * Создает секцию результатов тестирования.
     *
     * @return VBox с полем результатов и меткой статуса
     */
    private VBox createTestResultSection() {
        VBox box = new VBox(10);

        estimateStatusLabel = new Label("Готов к запуску тестов");
        estimateStatusLabel.setStyle("-fx-font-size: 11;");

        Label resultLabel = new Label("Результат:");
        resultLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");

        estimateResultArea = new TextArea();
        estimateResultArea.setWrapText(true);
        estimateResultArea.setPrefHeight(400);
        estimateResultArea.setEditable(false);
        estimateResultArea.setStyle("-fx-control-inner-background: #f5f5f5; " +
                "-fx-font-family: 'Courier New'; -fx-font-size: 10;");

        box.getChildren().addAll(estimateStatusLabel, resultLabel, estimateResultArea);

        logger.debug("Секция результатов тестов создана");
        return box;
    }

    /**
     * Валидирует входные данные, создаёт конфигурацию и генерирует пароль.
     */
    private void handleGeneratePassword() {
        long startTime = System.currentTimeMillis();
        logger.debug("handleGeneratePassword: начало обработки");

        try {
            String lengthText = generateLengthField.getText().trim();

            if (lengthText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Ошибка ввода",
                        "Пожалуйста, введите длину пароля");
                logger.warn("Поле длины пустое в GUI");
                return;
            }

            int length;
            try {
                length = Integer.parseInt(lengthText);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Ошибка ввода",
                        "Длина должна быть числом. Получено: " + lengthText);
                logger.warn("Некорректное число в поле длины: {}", lengthText, e);
                return;
            }

            if (length <= 0) {
                showAlert(Alert.AlertType.ERROR, "Ошибка ввода",
                        "Длина должна быть положительной");
                logger.warn("Введена неположительная длина: {}", length);
                return;
            }

            if (length > 1_000_000) {
                showAlert(Alert.AlertType.ERROR, "Ошибка ввода",
                        "Длина не должна превышать 1 000 000");
                logger.warn("Введена слишком большая длина: {}", length);
                return;
            }

            PasswordGenerationConfig config = new PasswordGenerationConfig(length);
            config.setUseLatin(generateLatinBox.isSelected());
            config.setUseCyrillic(generateCyrillicBox.isSelected());
            config.setUseDigits(generateDigitsBox.isSelected());
            config.setUseSpecial(generateSpecialBox.isSelected());

            String required = generateRequiredField.getText();
            if (required != null && !required.isEmpty()) {
                try {
                    required.chars().forEach(c -> config.addRequiredCharacter((char) c));
                    logger.debug("Добавлены обязательные символы в GUI: {}", required);
                } catch (Exception e) {
                    logger.error("Ошибка при добавлении обязательных символов в GUI", e);
                    showAlert(Alert.AlertType.ERROR, "Ошибка",
                            "Ошибка при добавлении символов: " + e.getMessage());
                    return;
                }
            }

            try {
                logger.info("Начало генерации пароля в GUI: длина={}, обязательных={}",
                        length, config.getRequiredCharacters().size());

                String password = generator.generate(config);
                long duration = System.currentTimeMillis() - startTime;

                String result = "Пароль:\n" + password + "\n\nДлина: "
                        + password.length() + " символов";
                generateResultArea.setText(result);

                logger.info("Пароль успешно сгенерирован в GUI за {}ms. Длина: {}",
                        duration, password.length());

            } catch (InvalidPasswordConfigException e) {
                logger.error("Ошибка конфигурации при генерации в GUI", e);
                showAlert(Alert.AlertType.ERROR, "Ошибка конфигурации",
                        "Проверьте параметры пароля:\n\n" + e.getMessage());

            } catch (Exception e) {
                logger.error("Неожиданная ошибка при генерации пароля в GUI", e);
                showAlert(Alert.AlertType.ERROR, "Критическая ошибка",
                        "Произошла неожиданная ошибка:\n\n" + e.getMessage());
            }

        } catch (Exception e) {
            logger.error("Неожиданная ошибка в handleGeneratePassword", e);
            showAlert(Alert.AlertType.ERROR, "Критическая ошибка",
                    "Произошла неожиданная ошибка:\n\n" + e.getMessage());
        }
    }

    /**
     * Запускает быстрый тест на 3 контрольных точках в отдельном потоке.
     */
    private void handleQuickTestAsync() {
        logger.info("Запуск быстрого теста из GUI");
        disableTestButtons(true);
        estimateStatusLabel.setText("Быстрый тест выполняется...");
        estimateResultArea.setText("Выполняется быстрый тест...\nПожалуйста, подождите...");

        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                logger.debug("Быстрый тест запущен в отдельном потоке");
                return estimator.runQuickTest();
            }
        };

        task.setOnSucceeded(event -> {
            String result = task.getValue();
            estimateResultArea.setText(result);
            estimateStatusLabel.setText("Быстрый тест завершен!");
            logger.info("Быстрый тест завершен успешно в GUI");
            disableTestButtons(false);
        });

        task.setOnFailed(event -> {
            logger.error("Ошибка при выполнении быстрого теста", task.getException());
            showAlert(Alert.AlertType.ERROR, "Ошибка теста",
                    "Ошибка: " + task.getException().getMessage());
            estimateResultArea.setText("Ошибка при выполнении теста");
            estimateStatusLabel.setText("Ошибка при выполнении теста");
            disableTestButtons(false);
        });

        new Thread(task).start();
    }

    /**
     * Запускает детальный тест в отдельном потоке.
     */
    private void handleDetailedTestAsync() {
        logger.info("Запуск детального теста из GUI");
        disableTestButtons(true);
        estimateResultArea.setText("""
                Выполняется детальный тест...
                Это может занять несколько минут.
                Пожалуйста, подождите...""");
        estimateStatusLabel.setText("Детальный тест выполняется...");

        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                logger.debug("Детальный тест запущен в отдельном потоке");
                return estimator.runDetailedTest();
            }
        };

        task.setOnSucceeded(event -> {
            String result = task.getValue();
            estimateResultArea.setText(result);
            estimateStatusLabel.setText("Детальный тест завершен!");
            logger.info("Детальный тест завершен успешно в GUI");
            disableTestButtons(false);
        });

        task.setOnFailed(event -> {
            logger.error("Ошибка при выполнении детального теста", task.getException());
            showAlert(Alert.AlertType.ERROR, "Ошибка теста",
                    "Ошибка: " + task.getException().getMessage());
            estimateResultArea.setText("Ошибка при выполнении теста");
            estimateStatusLabel.setText("Ошибка при выполнении теста");
            disableTestButtons(false);
        });

        new Thread(task).start();
    }

    /**
     * Запрашивает у пользователя параметры и выполняет тест в отдельном потоке.
     */
    private void handleCustomTestAsync() {
        logger.info("Запуск пользовательского теста из GUI");
        Dialog<int[]> dialog = new Dialog<>();
        dialog.setTitle("Пользовательский тест");
        dialog.setHeaderText("Введите параметры диапазона");

        TextField minField = new TextField();
        minField.setPromptText("Рекомендуется, >= 10 000");

        TextField maxField = new TextField();
        maxField.setPromptText("<= 1 000 000");

        TextField stepField = new TextField();
        stepField.setPromptText("Чем больше, тем быстрее");

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.getChildren().addAll(
                new Label("Минимальная длина:"), minField,
                new Label("Максимальная длина:"), maxField,
                new Label("Шаг:"), stepField
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    int min = Integer.parseInt(minField.getText());
                    int max = Integer.parseInt(maxField.getText());
                    int step = Integer.parseInt(stepField.getText());
                    return new int[]{min, max, step};
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Ошибка ввода",
                            "Все значения должны быть числами");
                    logger.warn("Ошибка парсинга чисел в пользовательском тесте", e);
                    return null;
                }
            }
            return null;
        });

        var result = dialog.showAndWait();

        if (result.isPresent()) {
            int[] params = result.get();
            int min = params[0], max = params[1], step = params[2];

            disableTestButtons(true);
            estimateStatusLabel.setText("Пользовательский тест выполняется...");
            estimateResultArea.setText("Выполняется пользовательский тест...\n" +
                    "Диапазон: " + min + " - " + max + ", шаг: " + step + "\n" +
                    "Пожалуйста, подождите...");

            Task<String> task = new Task<>() {
                @Override
                protected String call() {
                    logger.debug("Пользовательский тест запущен в отдельном потоке");
                    return estimator.runCustomTest(min, max, step);
                }
            };

            task.setOnSucceeded(event -> {
                String testResult = task.getValue();
                estimateResultArea.setText(testResult);
                estimateStatusLabel.setText("Пользовательский тест завершен!");
                logger.info("Пользовательский тест завершен успешно в GUI");
                disableTestButtons(false);
            });

            task.setOnFailed(event -> {
                logger.error("Ошибка при выполнении пользовательского теста", task.getException());
                showAlert(Alert.AlertType.ERROR, "Ошибка теста",
                        "Ошибка: " + task.getException().getMessage());
                estimateResultArea.setText("Ошибка при выполнении теста");
                estimateStatusLabel.setText("Ошибка при выполнении теста");
                disableTestButtons(false);
            });

            new Thread(task).start();
        }
    }

    /**
     * Отключает/включает кнопки тестов для предотвращения запуска нескольких тестов одновременно.
     *
     * @param disabled true для отключения, false для включения
     */
    private void disableTestButtons(boolean disabled) {
        quickTestButton.setDisable(disabled);
        detailedTestButton.setDisable(disabled);
        customTestButton.setDisable(disabled);
    }

    /**
     * Применяет стиль границы к компоненту.
     *
     * @param region компонент для стилизации
     */
    private void applyBorderStyle(Region region) {
        region.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5;");
    }

    /**
     * Показывает диалоговое окно с сообщением.
     *
     * @param alertType тип алерта
     * @param title заголовок диалога
     * @param message сообщение
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
