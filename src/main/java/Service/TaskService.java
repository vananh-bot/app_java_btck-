package Service;

import DAO.TaskAssignmentDAO;
import DAO.TaskDAO;
import Model.Task;
import Model.TaskDashboardDTO;
import Enum.Priority;
import Enum.TaskStatus;
import java.time.LocalDateTime;
import java.util.List;

public class TaskService {
    private TaskDAO taskDAO;
    private TaskAssignmentDAO taskAssignmentDAO;

    public TaskService(TaskDAO taskDAO,
                       TaskAssignmentDAO taskAssignmentDAO) {

        this.taskDAO = taskDAO;
        this.taskAssignmentDAO = taskAssignmentDAO;
    }

    public TaskService(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    public String createTask(String title, String description, Priority priority, TaskStatus taskStatus, LocalDateTime deadline, int projectId) {
        if(title == null || title.isBlank()){
            return "Vui lòng nhập tên công việc";
        }

        if(title.length() > 255){
            return "Tên đăng nhập quá dài";
        }

        if(taskDAO.existsByTitleAndProject(title, projectId)){
            return "Tên công việc bị trùng";
        }

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setStatus(taskStatus);
        task.setDeadline(deadline);
        task.setProjectId(projectId);

        taskDAO.insert2(task);
        return "SUCCESS";
    }

    public List<TaskDashboardDTO> getDashboardMyTask(int userId){
        List<TaskDashboardDTO> dashboardMyTask = taskDAO.getDashboardMyTask(userId);
        return dashboardMyTask;
    }
}
