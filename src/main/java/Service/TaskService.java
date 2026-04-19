package Service;

import DAO.ProjectDAO;
import DAO.SubTaskDAO;
import DAO.TaskDAO;
import Model.Comment;
import Model.Project;
import Model.SubTask;
import Model.Task;
import DAO.CommentDAO;

import java.util.List;

public class TaskService {
    private final ProjectDAO projectDAO = new ProjectDAO();
    private final TaskDAO taskDAO = new TaskDAO();
    private final SubTaskDAO subTaskDAO = new SubTaskDAO();
    private final CommentDAO commentDAO = new CommentDAO();

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

    public Project getProjectByTaskId(int taskId) {
        Task task = taskDAO.getById(taskId);
        if (task == null) return null;

        return projectDAO.findById(task.getProjectId()); // ✅ đúng
    }

    public String getProjectNameByTaskId(int taskId) {
        Task task = taskDAO.getById(taskId);
        if (task == null) return null;

        Project p = projectDAO.findById(task.getProjectId()); // ✅ đúng
        return p != null ? p.getName() : null;
    }

}

