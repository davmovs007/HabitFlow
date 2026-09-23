package com.habittracker.dao;
import com.habittracker.entity.Habit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HabitRepository {
    public List<Habit> getAllHabits() {
        List<Habit> habits = new ArrayList<>();
        String sql = "SELECT * FROM habits ORDER BY id DESC";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                habits.add(new Habit(rs.getInt("id"), rs.getString("title"), rs.getString("category"),
                        rs.getString("color_hex"), LocalDate.parse(rs.getString("created_at"))));
            }
        } catch (Exception e) {}
        return habits;
    }

    public void addHabit(Habit habit) {
        String sql = "INSERT INTO habits (title, category, color_hex, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, habit.getTitle()); pstmt.setString(2, habit.getCategory());
            pstmt.setString(3, habit.getColorHex()); pstmt.setString(4, habit.getCreatedAt().toString());
            pstmt.executeUpdate();
        } catch (Exception e) {}
    }

    public void deleteHabit(int id) {
        String sql = "DELETE FROM habits WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (Exception e) {}
    }

    public Set<LocalDate> getCompletedDates(int habitId) {
        Set<LocalDate> dates = new HashSet<>();
        String sql = "SELECT completed_date FROM habit_logs WHERE habit_id = ? AND is_completed = 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, habitId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) dates.add(LocalDate.parse(rs.getString("completed_date")));
            }
        } catch (Exception e) {}
        return dates;
    }

    public void setHabitCompleted(int habitId, LocalDate date, boolean completed) {
        String deleteSql = "DELETE FROM habit_logs WHERE habit_id = ? AND completed_date = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setInt(1, habitId); pstmt.setString(2, date.toString());
            pstmt.executeUpdate();
        } catch (Exception e) {}

        if (completed) {
            String insertSql = "INSERT INTO habit_logs (habit_id, completed_date, is_completed) VALUES (?, ?, 1)";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setInt(1, habitId); pstmt.setString(2, date.toString());
                pstmt.executeUpdate();
            } catch (Exception e) {}
        }
    }
    
    public int getTotalCompletions() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM habit_logs WHERE is_completed = 1";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) count = rs.getInt(1);
        } catch (Exception e) {}
        return count;
    }
}
