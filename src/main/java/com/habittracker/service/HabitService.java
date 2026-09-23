package com.habittracker.service;
import com.habittracker.dao.HabitRepository;
import com.habittracker.entity.Habit;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class HabitService {
    private HabitRepository repository;
    public HabitService() { this.repository = new HabitRepository(); }
    public List<Habit> getAllHabits() { return repository.getAllHabits(); }
    public void addHabit(Habit habit) { repository.addHabit(habit); }
    public void deleteHabit(int id) { repository.deleteHabit(id); }
    public boolean isCompletedToday(int habitId) { return repository.getCompletedDates(habitId).contains(LocalDate.now()); }
    public void setCompleted(int habitId, LocalDate date, boolean completed) { repository.setHabitCompleted(habitId, date, completed); }
    public int getCurrentStreak(int habitId) { return StreakCalculator.calculateCurrentStreak(repository.getCompletedDates(habitId)); }
    public int getMonthlyGoal(int habitId) { return StreakCalculator.calculateMonthlyGoalPercentage(repository.getCompletedDates(habitId)); }
    
    public boolean[] getLast7Days(int habitId) {
        Set<LocalDate> dates = repository.getCompletedDates(habitId);
        boolean[] last7 = new boolean[7];
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            last7[6 - i] = dates.contains(today.minusDays(i));
        }
        return last7;
    }
    
    public int getTotalCompletions() {
        return repository.getTotalCompletions();
    }
    
    public int getMaxStreak() {
        int max = 0;
        for (Habit h : getAllHabits()) {
            int s = getCurrentStreak(h.getId());
            if (s > max) max = s;
        }
        return max;
    }
}
