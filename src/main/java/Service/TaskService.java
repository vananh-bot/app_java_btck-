package Service;

import DAO.TaskAssignmentDAO;
import DAO.TaskDAO;
import Model.Task;
import Model.TaskDashboardDTO;

import java.util.List;

public class TaskService {
    private TaskDAO taskDAO;
    private TaskAssignmentDAO taskAssignmentDAO;

    public TaskService(TaskDAO taskDAO,
                       TaskAssignmentDAO taskAssignmentDAO) {

        this.taskDAO = taskDAO;
        this.taskAssignmentDAO = taskAssignmentDAO;
    }

    public List<TaskDashboardDTO> getDashboardMyTask(int userId){
        List<TaskDashboardDTO> dashboardMyTask = taskDAO.getDashboardMyTask(userId);
        return dashboardMyTask;
    }
}

