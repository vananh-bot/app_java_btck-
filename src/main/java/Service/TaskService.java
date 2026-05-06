package Service;

import Cache.ProjectCache;
import Cache.TaskCache;
import DAO.CommentDAO;
import DAO.ProjectDAO;
import DAO.SubTaskDAO;
import DAO.TaskDAO;
import DTO.ProjectDashboardDTO;
import Model.Comment;
import Model.Project;
import Model.SubTask;
import Model.Task;
import DTO.TaskDashboardDTO;
import Enum.Priority;
import Enum.TaskStatus;
import java.time.LocalDateTime;
import java.util.List;

public class TaskService {
    private TaskDAO taskDAO;
    private final ProjectService projectService = new ProjectService(new ProjectDAO());
    private final SubTaskDAO subTaskDAO = new SubTaskDAO();
    private final CommentDAO commentDAO = new CommentDAO();

    private TaskCache taskCache = TaskCache.getInstance();
    private ProjectCache projectCache = ProjectCache.getInstance();

    public TaskService(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    public int createTask(String title, String description, Priority priority, TaskStatus taskStatus, LocalDateTime deadline, int projectId) {
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

        Task fullTask = getTaskById(taskId);
        taskCache.put(fullTask);

        return taskId;
    }

    public List<TaskDashboardDTO> getDashboardMyTask(int userId){
        List<TaskDashboardDTO> dashboardMyTask = taskDAO.getDashboardMyTask(userId);
        return dashboardMyTask;
    }

    public Task getTaskById(int taskId) {
        Task cached = taskCache.get(taskId);

        if(cached != null) return cached;

        Task task = taskDAO.getById(taskId);
        if(task != null) taskCache.put(task);

        return task;
    }
    public String getProjectNameByProjectId(int projectId) {
        return projectService.getProjectName(projectId);
    }

    public void updateTask(Task task) {
        taskDAO.update(task);
    }

    // ================= SUB TASK =================
    public List<SubTask> getSubTasks(int taskId) {
        return subTaskDAO.findByTaskId(taskId);
    }

    public void toggleSubTask(int subTaskId, boolean completed) {
        subTaskDAO.updateStatus(subTaskId, completed);
    }

    public void addSubTask(SubTask subTask) {
        subTaskDAO.insert(subTask);
    }

    public void deleteSubTask(int subTaskId) {
        subTaskDAO.delete(subTaskId);
    }


    public List<Comment> getComments(int taskId) {
        return commentDAO.getByTaskId(taskId);
    }

    public boolean addComment(Comment c) {
        return commentDAO.insert(c);
    }

    public void deleteComment(int id) {
        commentDAO.deleteById(id);
    }

    public void updateComment(Comment c) {
        commentDAO.update(c);
    }

    public int countComments(int taskId) {
        return commentDAO.countByTaskId(taskId);
    }
}