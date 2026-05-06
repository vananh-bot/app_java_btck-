package Service;

import DAO.TaskDAO;
import Model.Task;
import DTO.TaskDashboardDTO;
import Enum.Priority;
import Enum.TaskStatus;
import java.time.LocalDateTime;
import java.util.List;

public class TaskService {
    private TaskDAO taskDAO;
    private MailService mailservice = new MailService();
    public TaskService(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    public int createTask(String title, String description, Priority priority, TaskStatus taskStatus, LocalDateTime deadline, int projectId, String assignerName) {
        if(title == null || title.isBlank()){
            throw new IllegalArgumentException("Vui lòng nhập tên công việc");
        }

        if(title.length() > 255){
            throw new IllegalArgumentException("Tên đăng nhập quá dài");
        }

        if(taskDAO.existsByTitleAndProject(title, projectId)){
            throw new IllegalArgumentException("Tên công việc bị trùng");
        }

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setStatus(taskStatus);
        task.setDeadline(deadline);
        task.setProjectId(projectId);

        int taskId = taskDAO.insert(task); //vua insert vua lay taskid
        if (taskId > 0) {
            String projectName = taskDAO.getProjectNameById(projectId);

            List<String> allMemberEmails = taskDAO.getEmailsInProject(projectId);
            String deadlineStr = (deadline != null) ? deadline.toString().replace("T", " ") : "Không có hạn";
            if (allMemberEmails != null && !allMemberEmails.isEmpty()) {
                for (String email : allMemberEmails) {
                    mailservice.sendNewTaskAssignment(
                            email,
                            projectName,
                            title,
                            assignerName,
                            deadlineStr
                    );
                }
            }
        }
        return taskId;
    }

    public List<TaskDashboardDTO> getDashboardMyTask(int userId){
        List<TaskDashboardDTO> dashboardMyTask = taskDAO.getDashboardMyTask(userId);
        return dashboardMyTask;
    }

    public Task getTaskById(int taskId) {
        return taskDAO.getById(taskId);
    }
}
