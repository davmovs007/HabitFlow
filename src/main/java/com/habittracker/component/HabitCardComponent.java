package com.habittracker.component;

import com.habittracker.entity.Habit;
import com.habittracker.service.HabitService;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class HabitCardComponent extends VBox {

    public HabitCardComponent(Habit habit, HabitService service, Runnable onUpdate,
                              boolean isEn, boolean isDark) {
        this.getStyleClass().add("habit-card");

        String textColor = isDark ? "#F1F5F9" : "#0F172A";
        String mutedColor = isDark ? "#94A3B8" : "#64748B";
        String dotOff = isDark ? "#475569" : "#CBD5E1";

        // ── Header: checkbox + title + delete ─────────────────────
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        CheckBox checkBox = new CheckBox();
        checkBox.getStyleClass().add("custom-checkbox");
        boolean completedToday = service.isCompletedToday(habit.getId());
        checkBox.setSelected(completedToday);
        checkBox.setOnAction(e -> {
            service.setCompleted(habit.getId(), LocalDate.now(), checkBox.isSelected());
            if (onUpdate != null) onUpdate.run();
        });

        VBox titleBox = new VBox(3);
        Label title = new Label(habit.getTitle());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + textColor + ";");
        Label category = new Label(habit.getCategory());
        category.setStyle("-fx-font-size: 13px; -fx-text-fill: " + mutedColor + ";");
        titleBox.getChildren().addAll(title, category);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button deleteBtn = new Button("✕");
        deleteBtn.getStyleClass().add("delete-button");
        deleteBtn.setOnAction(e -> {
            service.deleteHabit(habit.getId());
            if (onUpdate != null) onUpdate.run();
        });

        header.getChildren().addAll(checkBox, titleBox, spacer, deleteBtn);

        // ── 7-day history ─────────────────────────────────────────
        VBox historySection = new VBox(10);
        historySection.setStyle("-fx-padding: 20 0 10 0;");

        HBox weekBox = new HBox(12);
        boolean[] last7 = service.getLast7Days(habit.getId());
        LocalDate today = LocalDate.now();
        Locale loc = isEn ? Locale.ENGLISH : new Locale("ru");

        for (int i = 6; i >= 0; i--) {
            VBox dayBox = new VBox(6);
            dayBox.setAlignment(Pos.CENTER);
            LocalDate d = today.minusDays(i);
            String dayName = d.getDayOfWeek().getDisplayName(TextStyle.SHORT, loc).toUpperCase(loc);
            Label dayLbl = new Label(dayName);
            dayLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: " + mutedColor + "; -fx-font-weight: bold;");

            Circle dot = new Circle(8);
            dot.setFill(last7[6 - i] ? Color.web(habit.getColorHex()) : Color.web(dotOff));

            dayBox.getChildren().addAll(dayLbl, dot);
            weekBox.getChildren().add(dayBox);
        }
        historySection.getChildren().add(weekBox);

        // ── Bottom: streak + progress ─────────────────────────────
        HBox bottomBox = new HBox();
        bottomBox.setAlignment(Pos.CENTER_LEFT);
        bottomBox.setStyle("-fx-padding: 15 0 0 0;");

        int streak = service.getCurrentStreak(habit.getId());
        Label streakLabel = new Label("🔥 " + streak + (isEn ? " days" : " дней"));
        streakLabel.setStyle("-fx-font-weight: 800; -fx-text-fill: #F59E0B;"
                + " -fx-background-color: #FEF3C7; -fx-padding: 6 14;"
                + " -fx-background-radius: 20; -fx-font-size: 13px;");

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        VBox progressBox = new VBox(6);
        progressBox.setAlignment(Pos.CENTER_RIGHT);
        int goal = service.getMonthlyGoal(habit.getId());
        Label goalLbl = new Label((isEn ? "Goal: " : "Цель: ") + goal + "%");
        goalLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + mutedColor + "; -fx-font-weight: 600;");

        ProgressBar pb = new ProgressBar(goal / 100.0);
        pb.setPrefWidth(120);
        pb.setStyle("-fx-accent: " + habit.getColorHex() + ";");

        progressBox.getChildren().addAll(goalLbl, pb);
        bottomBox.getChildren().addAll(streakLabel, spacer2, progressBox);

        this.getChildren().addAll(header, historySection, bottomBox);
    }
}
