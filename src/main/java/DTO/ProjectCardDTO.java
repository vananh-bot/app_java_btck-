package DTO;

import Model.Project;

public class ProjectCardDTO {
    private Project project; // Chứa thông tin gốc của Project
    private int todoCount;
    private int inProgressCount;
    private int doneCount;

    public ProjectCardDTO(Project project, int todoCount, int inProgressCount, int doneCount) {
        this.project = project;
        this.todoCount = todoCount;
        this.inProgressCount = inProgressCount;
        this.doneCount = doneCount;
    }

    // --- Getters ---
    public Project getProject() { return project; }
    public int getTodoCount() { return todoCount; }
    public int getInProgressCount() { return inProgressCount; }
    public int getDoneCount() { return doneCount; }

    // Tự động tính phần trăm tiến độ cho giao diện (từ 0.0 đến 1.0)
    public double getProgress() {
        int total = todoCount + inProgressCount + doneCount;
        if (total == 0) return 0.0;
        return (double) doneCount / total;
    }
}