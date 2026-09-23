package com.habittracker.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:habits.db";
    public static Connection getConnection() throws Exception {
        Connection conn = DriverManager.getConnection(URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }
    public static void initDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS habits (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title TEXT NOT NULL, " +
                    "category TEXT DEFAULT 'General', " +
                    "color_hex TEXT DEFAULT '#10B981', " +
                    "created_at TEXT NOT NULL" +
                    ")");
            stmt.execute("CREATE TABLE IF NOT EXISTS habit_logs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "habit_id INTEGER, " +
                    "completed_date TEXT NOT NULL, " +
                    "is_completed INTEGER CHECK (is_completed IN (0,1)), " +
                    "FOREIGN KEY(habit_id) REFERENCES habits(id) ON DELETE CASCADE" +
                    ")");
        } catch (Exception e) { e.printStackTrace(); }
    }
}
