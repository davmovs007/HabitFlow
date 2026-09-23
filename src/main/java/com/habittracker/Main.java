package com.habittracker;

import javafx.application.Application;

/**
 * Application entry point. Keep this class independent of {@link Application}
 * so it can be run directly by the IDE with the JavaFX Maven dependencies on
 * the classpath.
 */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        Application.launch(HabitTrackerApplication.class, args);
    }
}
