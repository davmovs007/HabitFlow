package com.habittracker;

import com.habittracker.dao.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** JavaFX lifecycle class, separated from the executable entry point. */
public class HabitTrackerApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseManager.initDatabase();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
        Scene scene = new Scene(loader.load(), 900, 650);
        primaryStage.setTitle("Habit Tracker Desktop");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
