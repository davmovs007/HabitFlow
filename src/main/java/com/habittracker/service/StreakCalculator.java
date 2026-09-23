package com.habittracker.service;
import java.time.LocalDate;
import java.util.Set;

public class StreakCalculator {
    public static int calculateCurrentStreak(Set<LocalDate> completedDates) {
        if (completedDates == null || completedDates.isEmpty()) return 0;
        LocalDate today = LocalDate.now();
        int streak = 0;

        if (!completedDates.contains(today)) {
            LocalDate yesterday = today.minusDays(1);
            if (!completedDates.contains(yesterday)) {
                return 0; // Missed today and yesterday
            }
            today = yesterday; // Retain streak and calculate from yesterday
        }

        while (completedDates.contains(today)) {
            streak++;
            today = today.minusDays(1);
        }
        return streak;
    }
    
    public static int calculateMonthlyGoalPercentage(Set<LocalDate> completedDates) {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();
        
        long completedThisMonth = completedDates.stream()
            .filter(d -> d.getYear() == year && d.getMonthValue() == month)
            .count();
            
        int daysInMonth = today.lengthOfMonth();
        return (int) Math.round((double) completedThisMonth / daysInMonth * 100);
    }
}
