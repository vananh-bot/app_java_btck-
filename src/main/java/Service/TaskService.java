package Service;

import DAO.SubTaskDAO;
import DAO.TaskDAO;
import Model.Comment;
import Model.SubTask;
import Model.Task;
import DAO.CommentDAO;
import Model.TaskDashboardDTO;
import DAO.TaskAssignmentDAO;

import java.util.List;

public class TaskService {
    private final TaskDAO taskDAO = new TaskDAO();
    private final SubTaskDAO subTaskDAO = new SubTaskDAO();
    private final CommentDAO commentDAO = new CommentDAO();
    private final TaskAssignmentDAO taskAssignmentDAO = new TaskAssignmentDAO();

    // ================= TASK =================
    public Task getTaskById(int taskId) {
        return taskDAO.getById(taskId);

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

    public TaskService(){

    }

    public List<TaskDashboardDTO> getDashboardMyTask(int userId){
        List<TaskDashboardDTO> dashboardMyTask = taskDAO.getDashboardMyTask(userId);
        return dashboardMyTask;
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

