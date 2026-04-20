package DTO;

import Model.Project;

public class ProjectCardDTO {
    private Project project; // Chứa thông tin gốc của Project
    private int todoCount;
    private int inProgressCount;
    private int doneCount;
    private double progress;

    public ProjectCardDTO(Project project, int todoCount, int inProgressCount, int doneCount) {
        this.project = project;
        this.todoCount = todoCount;
        this.inProgressCount = inProgressCount;
        this.doneCount = doneCount;
        calculateProgress();
    }

    public ProjectCardDTO() {
    }

    // --- Getters ---
    public Project getProject() {
        return project;
    }

    public int getTodoCount() {
        return todoCount;
    }

    public int getInProgressCount() {
        return inProgressCount;
    }

    public int getDoneCount() {
        return doneCount;
    }

    // Tự động tính phần trăm tiến độ cho giao diện (từ 0.0 đến 1.0)
    public double getProgress() {
        return progress;
    }

    public void setProject(Project project) {
        this.project = project;
        calculateProgress();

    }
    public void setTodoCount(int todoCount) {
        this.todoCount = todoCount;
        calculateProgress();
    }

    public void setInProgressCount(int inProgressCount) {
        this.inProgressCount = inProgressCount;
        calculateProgress();
    }

    public void setDoneCount(int doneCount) {
        this.doneCount = doneCount;
        calculateProgress();
    }

    private void calculateProgress() {
        int total = todoCount + inProgressCount + doneCount;
        this.progress = (total == 0) ? 0.0 : (double) doneCount / total;
    }
}