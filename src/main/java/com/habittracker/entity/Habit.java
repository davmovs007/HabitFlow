package com.habittracker.entity;
import java.time.LocalDate;
public class Habit {
    private int id;
    private String title;
    private String category;
    private String colorHex;
    private LocalDate createdAt;
    public Habit(int id, String title, String category, String colorHex, LocalDate createdAt) {
        this.id = id; this.title = title; this.category = category; this.colorHex = colorHex; this.createdAt = createdAt;
    }
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getColorHex() { return colorHex; }
    public LocalDate getCreatedAt() { return createdAt; }
}
