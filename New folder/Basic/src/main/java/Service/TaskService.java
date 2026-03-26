package Service;

import DAO.TaskAssignmentDAO;
import DAO.TaskDAO;

public class TaskService {
    private TaskDAO taskDAO;
    private TaskAssignmentDAO taskAssignmentDAO;

    public TaskService(TaskDAO taskDAO,
                       TaskAssignmentDAO taskAssignmentDAO) {

        this.taskDAO = taskDAO;
        this.taskAssignmentDAO = taskAssignmentDAO;
    }

    public void createTask(String title, int projectId, int creatorId) {

    }

    public void assignUser(int taskId, int userId) {

    }

    public void updateTask() {

    }

    public void updateDeadline() {

    }

    public void deleteTask(int taskId) {

    }

    public void updateStatus() {

    }
}

