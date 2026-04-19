package Controller;

import Service.TaskService;

public class TaskController {
    private TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    private int taskId;

    public void setTaskId(int taskId){
        this.taskId = taskId;
    }

    public void createTask() {

    }

    public void assignTask() {

    }

    public void updateTask() {

    }

    public void deleteTask() {

    }
}
