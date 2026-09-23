package com.habittracker.controller;

import com.habittracker.component.HabitCardComponent;
import com.habittracker.entity.Habit;
import com.habittracker.service.HabitService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import java.time.LocalDate;
import java.util.List;
import java.util.prefs.Preferences;

public class MainController {
    @FXML private BorderPane rootPane;
    @FXML private Button btnDash, btnStat, btnAchieve, btnSet;
    @FXML private VBox viewDashboard, viewAnalytics, viewAchievements, viewSettings;

    @FXML private Label lblDashTitle, lblStatTitle, lblTotalHabits, lblTotalCompletions, lblBestStreak;
    @FXML private Label lblAchieveTitle, lblSetTitle, lblSetGroup1, lblSetTheme;
    @FXML private Label lblSetGroup2, lblSetLang, lblSetTz, lblSetWeek, lblSetGroup3, lblSetDate, lblSetTime, lblSetGroup4, lblSetStart;
    @FXML private Button btnAddHabit;

    @FXML private FlowPane habitsContainer;
    @FXML private Label statsLabel;

    @FXML private Label statTotalHabits, statTotalCompletions, statBestStreak;
    @FXML private VBox achievementsContainer;

    @FXML private ComboBox<String> themeCombo, langCombo, tzCombo, weekCombo, dateCombo, timeCombo, startCombo;

    private HabitService habitService;
    private Preferences prefs;

    /**
     * Guard flag: when true, combo-box onAction handlers are suppressed.
     * This prevents the recursive cascade where updateComboOptions() → setAll()
     * fires onAction → saves index -1 → resets the language back to 0.
     */
    private boolean updatingUI = false;

    public void initialize() {
        habitService = new HabitService();
        prefs = Preferences.userNodeForPackage(MainController.class);

        setupHandlers();
        refreshEverything();

        int startIdx = prefs.getInt("startIdx", 0);
        switch (startIdx) {
            case 1 -> showAnalytics();
            case 2 -> showAchievements();
            case 3 -> showSettings();
            default -> showDashboard();
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private boolean isEn() {
        return prefs.getInt("langIdx", 0) == 1;
    }

    private boolean isDark() {
        return prefs.getInt("themeIdx", 0) == 1;
    }

    /**
     * Full UI refresh: repopulate combos, translate labels, reload data, apply theme.
     */
    private void refreshEverything() {
        updatingUI = true;
        try {
            populateComboBoxes();
            translateLabels();
        } finally {
            updatingUI = false;
        }
        applyTheme();
        reloadData();
    }

    private void reloadData() {
        loadHabits();
        updateAnalytics();
        updateAchievements();
    }

    // ── Combo boxes ─────────────────────────────────────────────────

    private void populateComboBoxes() {
        boolean en = isEn();

        themeCombo.getItems().setAll(en ? List.of("Light", "Dark", "System") : List.of("Светлая", "Тёмная", "Системная"));
        themeCombo.getSelectionModel().select(prefs.getInt("themeIdx", 0));

        langCombo.getItems().setAll("Русский", "English");
        langCombo.getSelectionModel().select(prefs.getInt("langIdx", 0));

        tzCombo.getItems().setAll(en
                ? List.of("UTC+3 (Moscow)", "UTC+4 (Samara)", "UTC+0 (London)")
                : List.of("UTC+3 (Москва)", "UTC+4 (Самара)", "UTC+0 (Лондон)"));
        tzCombo.getSelectionModel().select(prefs.getInt("tzIdx", 0));

        weekCombo.getItems().setAll(en ? List.of("Monday", "Sunday") : List.of("Понедельник", "Воскресенье"));
        weekCombo.getSelectionModel().select(prefs.getInt("weekIdx", 0));

        dateCombo.getItems().setAll("DD.MM.YYYY", "MM/DD/YYYY");
        dateCombo.getSelectionModel().select(prefs.getInt("dateIdx", 0));

        timeCombo.getItems().setAll("24h", "12h");
        timeCombo.getSelectionModel().select(prefs.getInt("timeIdx", 0));

        startCombo.getItems().setAll(en
                ? List.of("Dashboard", "Analytics", "Achievements")
                : List.of("Дашборд", "Аналитика", "Достижения"));
        startCombo.getSelectionModel().select(prefs.getInt("startIdx", 0));
    }

    /**
     * Handlers are set up ONCE and never re-created.  The `updatingUI` flag
     * ensures they don't accidentally fire while we repopulate items.
     */
    private void setupHandlers() {
        themeCombo.setOnAction(e -> {
            if (updatingUI) return;
            int i = themeCombo.getSelectionModel().getSelectedIndex();
            if (i >= 0) { prefs.putInt("themeIdx", i); applyTheme(); }
        });
        langCombo.setOnAction(e -> {
            if (updatingUI) return;
            int i = langCombo.getSelectionModel().getSelectedIndex();
            if (i >= 0) { prefs.putInt("langIdx", i); refreshEverything(); }
        });
        tzCombo.setOnAction(e -> {
            if (updatingUI) return;
            int i = tzCombo.getSelectionModel().getSelectedIndex();
            if (i >= 0) prefs.putInt("tzIdx", i);
        });
        weekCombo.setOnAction(e -> {
            if (updatingUI) return;
            int i = weekCombo.getSelectionModel().getSelectedIndex();
            if (i >= 0) prefs.putInt("weekIdx", i);
        });
        dateCombo.setOnAction(e -> {
            if (updatingUI) return;
            int i = dateCombo.getSelectionModel().getSelectedIndex();
            if (i >= 0) prefs.putInt("dateIdx", i);
        });
        timeCombo.setOnAction(e -> {
            if (updatingUI) return;
            int i = timeCombo.getSelectionModel().getSelectedIndex();
            if (i >= 0) prefs.putInt("timeIdx", i);
        });
        startCombo.setOnAction(e -> {
            if (updatingUI) return;
            int i = startCombo.getSelectionModel().getSelectedIndex();
            if (i >= 0) prefs.putInt("startIdx", i);
        });
    }

    // ── Translation ─────────────────────────────────────────────────

    private void translateLabels() {
        boolean en = isEn();

        // Sidebar
        btnDash.setText(en ? "🏠  Dashboard" : "🏠  Дашборд");
        btnStat.setText(en ? "📊  Analytics" : "📊  Аналитика");
        btnAchieve.setText(en ? "🏆  Achievements" : "🏆  Достижения");
        btnSet.setText(en ? "⚙  Settings" : "⚙  Настройки");

        // Dashboard
        lblDashTitle.setText(en ? "Dashboard" : "Дашборд");
        btnAddHabit.setText(en ? "+ New Habit" : "+ Новая привычка");

        // Analytics
        lblStatTitle.setText(en ? "Analytics" : "Аналитика");
        lblTotalHabits.setText(en ? "Total Habits" : "Всего привычек");
        lblTotalCompletions.setText(en ? "Total Completions" : "Всего выполнений");
        lblBestStreak.setText(en ? "Best Streak (days)" : "Лучшая серия (дней)");

        // Achievements
        lblAchieveTitle.setText(en ? "Achievements" : "Достижения");

        // Settings
        lblSetTitle.setText(en ? "Settings" : "Настройки");
        lblSetGroup1.setText(en ? "Appearance" : "Оформление");
        lblSetTheme.setText(en ? "App Theme:" : "Тема приложения:");
        lblSetGroup2.setText(en ? "Regional" : "Региональные данные");
        lblSetLang.setText(en ? "Language:" : "Язык интерфейса:");
        lblSetTz.setText(en ? "Timezone:" : "Часовой пояс:");
        lblSetWeek.setText(en ? "First day of week:" : "Первый день недели:");
        lblSetGroup3.setText(en ? "Formats" : "Форматы");
        lblSetDate.setText(en ? "Date format:" : "Формат даты:");
        lblSetTime.setText(en ? "Time format:" : "Формат времени:");
        lblSetGroup4.setText(en ? "Navigation" : "Навигация");
        lblSetStart.setText(en ? "Start screen:" : "Стартовый экран:");
    }

    // ── Theme ───────────────────────────────────────────────────────

    private void applyTheme() {
        if (isDark()) {
            if (!rootPane.getStyleClass().contains("dark-mode")) {
                rootPane.getStyleClass().add("dark-mode");
            }
        } else {
            rootPane.getStyleClass().remove("dark-mode");
        }
    }

    // ── Navigation ──────────────────────────────────────────────────

    @FXML public void showDashboard()    { switchView(viewDashboard, btnDash); loadHabits(); }
    @FXML public void showAnalytics()    { switchView(viewAnalytics, btnStat); updateAnalytics(); }
    @FXML public void showAchievements() { switchView(viewAchievements, btnAchieve); updateAchievements(); }
    @FXML public void showSettings()     { switchView(viewSettings, btnSet); }

    private void switchView(VBox view, Button btn) {
        for (VBox v : List.of(viewDashboard, viewAnalytics, viewAchievements, viewSettings)) {
            v.setVisible(false);
            v.setManaged(false);
        }
        view.setVisible(true);
        view.setManaged(true);

        for (Button b : List.of(btnDash, btnStat, btnAchieve, btnSet)) {
            b.getStyleClass().remove("sidebar-btn-active");
        }
        btn.getStyleClass().add("sidebar-btn-active");
    }

    // ── Dashboard ───────────────────────────────────────────────────

    private void loadHabits() {
        habitsContainer.getChildren().clear();
        List<Habit> habits = habitService.getAllHabits();
        int completedToday = 0;
        for (Habit habit : habits) {
            if (habitService.isCompletedToday(habit.getId())) completedToday++;
            habitsContainer.getChildren().add(
                    new HabitCardComponent(habit, habitService, this::reloadData, isEn(), isDark()));
        }
        statsLabel.setText(isEn()
                ? "Completed today: " + completedToday + " of " + habits.size()
                : "Сегодня выполнено: " + completedToday + " из " + habits.size());
    }

    @FXML
    public void handleAddHabit() {
        boolean en = isEn();
        Dialog<Habit> dialog = new Dialog<>();
        dialog.setTitle(en ? "New Habit" : "Новая привычка");
        dialog.setHeaderText(en ? "Create a new habit 🚀" : "Создайте новую привычку 🚀");
        try {
            dialog.getDialogPane().getStylesheets()
                    .add(getClass().getResource("/css/style.css").toExternalForm());
        } catch (Exception ignored) {}

        ButtonType saveBtn = new ButtonType(en ? "Add" : "Добавить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextField title = new TextField();
        title.setPromptText(en ? "e.g. Reading 30 mins" : "Например: Чтение 30 минут");
        title.setStyle("-fx-pref-width: 260; -fx-padding: 10; -fx-background-radius: 8; -fx-font-size: 14px;");

        ComboBox<String> category = new ComboBox<>();
        if (en) category.getItems().addAll("Health", "Sport", "Study", "Work", "Creative", "Growth", "Other");
        else    category.getItems().addAll("Здоровье", "Спорт", "Учёба", "Работа", "Творчество", "Саморазвитие", "Другое");
        category.getSelectionModel().select(0);
        category.setStyle("-fx-pref-width: 260;");

        ColorPicker colorPicker = new ColorPicker(Color.web("#3B82F6"));
        colorPicker.setStyle("-fx-pref-width: 260;");

        grid.add(new Label(en ? "Title:" : "Название:"), 0, 0);
        grid.add(title, 1, 0);
        grid.add(new Label(en ? "Category:" : "Категория:"), 0, 1);
        grid.add(category, 1, 1);
        grid.add(new Label(en ? "Color:" : "Цвет:"), 0, 2);
        grid.add(colorPicker, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == saveBtn && !title.getText().trim().isEmpty()) {
                String hex = String.format("#%02X%02X%02X",
                        (int) (colorPicker.getValue().getRed() * 255),
                        (int) (colorPicker.getValue().getGreen() * 255),
                        (int) (colorPicker.getValue().getBlue() * 255));
                return new Habit(0, title.getText().trim(), category.getValue(), hex, LocalDate.now());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(habit -> {
            habitService.addHabit(habit);
            reloadData();
        });
    }

    // ── Analytics ───────────────────────────────────────────────────

    private void updateAnalytics() {
        statTotalHabits.setText(String.valueOf(habitService.getAllHabits().size()));
        statTotalCompletions.setText(String.valueOf(habitService.getTotalCompletions()));
        statBestStreak.setText(String.valueOf(habitService.getMaxStreak()));
    }

    // ── Achievements ────────────────────────────────────────────────

    private void updateAchievements() {
        achievementsContainer.getChildren().clear();
        int total = habitService.getTotalCompletions();
        int maxStreak = habitService.getMaxStreak();
        boolean en = isEn();
        boolean dark = isDark();

        achievementsContainer.getChildren().addAll(
            createAchieveCard(
                en ? "🏆  First Step" : "🏆  Первый шаг",
                en ? "Complete your first habit" : "Выполните привычку впервые",
                total, 1, dark),
            createAchieveCard(
                en ? "🔥  Consistency" : "🔥  Стабильность",
                en ? "Reach a 7-day streak" : "Достигните серии в 7 дней",
                maxStreak, 7, dark),
            createAchieveCard(
                en ? "🏅  Marathoner" : "🏅  Марафонец",
                en ? "Log 50 completions" : "Наберите 50 отметок о выполнении",
                total, 50, dark),
            createAchieveCard(
                en ? "🚀  Legend" : "🚀  Легенда",
                en ? "Reach a 30-day streak" : "Достигните серии в 30 дней",
                maxStreak, 30, dark)
        );
    }

    private HBox createAchieveCard(String title, String desc, int current, int target, boolean dark) {
        String textColor = dark ? "#F1F5F9" : "#0F172A";
        String mutedColor = dark ? "#94A3B8" : "#64748B";

        HBox card = new HBox(15);
        card.getStyleClass().add("achievement-card");
        card.setAlignment(Pos.CENTER_LEFT);

        VBox texts = new VBox(5);
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + textColor + ";");
        Label d = new Label(desc);
        d.setStyle("-fx-font-size: 14px; -fx-text-fill: " + mutedColor + ";");
        texts.getChildren().addAll(t, d);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox prog = new VBox(5);
        prog.setAlignment(Pos.CENTER_RIGHT);
        int clamped = Math.min(current, target);
        Label p = new Label(clamped + " / " + target);
        p.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + textColor + ";");

        ProgressBar bar = new ProgressBar((double) clamped / target);
        bar.setPrefWidth(130);
        if (clamped >= target) {
            bar.setStyle("-fx-accent: #10B981;");
            t.setText(title + "  ✅");
        } else {
            bar.setStyle("-fx-accent: #3B82F6;");
        }
        prog.getChildren().addAll(p, bar);

        card.getChildren().addAll(texts, spacer, prog);
        return card;
    }
}
