package DAO;

import Model.Comment;
import Model.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.JDBCUtil;
import Enum.TaskStatus;
import Enum.Priority;

public class TaskDAO implements TaskInterfaceDAO<Task>{

    @Override
    public int insert(Task task){
        String sql = "INSERT INTO tasks (title, description, status, priority, deadline, project_id, created_by) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try(
            Connection connection = JDBCUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
        ){
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getStatus().name());
            ps.setString(4, task.getPriority().name());
            ps.setTimestamp(5, Timestamp.valueOf(task.getDeadline()));
            ps.setInt(6, task.getProjectId());
            ps.setInt(7, task.getCreatedBy());

            return ps.executeUpdate();

        } catch (SQLException e){
            throw new RuntimeException("Insert task failed", e);
        }
    }
    @Override
    public boolean deleteById(int id){
        String sql = "DELETE FROM tasks WHERE id = ?";

        try(
                Connection connection = JDBCUtil.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ){
            ps.setInt(1, id);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e){
            throw new RuntimeException("Insert task failed", e);
        }

    }

    @Override
    public int update(Task task){
        String sql = "UPDATE tasks SET title = ?, description = ?, status = ?, priority = ?, deadline = ? WHERE id = ?";

        try(
                Connection connection = JDBCUtil.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ){
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getStatus().name());
            ps.setString(4, task.getPriority().name());
            ps.setTimestamp(5, Timestamp.valueOf(task.getDeadline()));
            ps.setInt(6, task.getId());

            return ps.executeUpdate();

        } catch (SQLException e){
            throw new RuntimeException("Update task failed", e);
        }
    }

    @Override
    public Task getById(int id){
        String sql = "SELECT * FROM tasks WHERE id = ?";

        try(
            Connection connection = JDBCUtil.getConnection();
            PreparedStatement ps =connection.prepareStatement(sql);)
        {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                Task task = new Task();

                task.setId(rs.getInt("id"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setStatus(TaskStatus.valueOf(rs.getString("status")));
                task.setPriority(Priority.valueOf(rs.getString("priority")));
                task.setDeadline(rs.getTimestamp("deadline").toLocalDateTime());
                task.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                task.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

                return task;
            }

        } catch (SQLException e){
            throw new RuntimeException("getById task failed", e);
        }

        return null;
    }

    @Override
    public List<Task> getTasksByProjectId(int projectId){
        List<Task> list = new ArrayList<>();

        String sql = "SELECT * FROM tasks WHERE project_id = ?";

        try(
                Connection connection = JDBCUtil.getConnection();
                PreparedStatement ps =connection.prepareStatement(sql);)
        {
            ps.setInt(1, projectId);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                Task task = new Task();

                task.setId(rs.getInt("id"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setStatus(TaskStatus.valueOf(rs.getString("status")));
                task.setPriority(Priority.valueOf(rs.getString("priority")));
                task.setDeadline(rs.getTimestamp("deadline").toLocalDateTime());
                task.setProjectId(rs.getInt("project_id"));
                task.setCreatedBy(rs.getInt("created_by"));

                list.add(task);

            }

        } catch (SQLException e){
            throw new RuntimeException("GetTasksByIdProject failed", e);
        }

        return list;
    }

    @Override
    public List<Task> getTasksByPriority(Priority priority){
        List<Task> list = new ArrayList<>();

        String sql = "SELECT * FROM tasks WHERE priority = ?";

        try(
                Connection connection = JDBCUtil.getConnection();
                PreparedStatement ps =connection.prepareStatement(sql);)
        {
            ps.setString(1, priority.name());
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                Task task = new Task();

                task.setId(rs.getInt("id"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setStatus(TaskStatus.valueOf(rs.getString("status")));
                task.setPriority(Priority.valueOf(rs.getString("priority")));
                task.setDeadline(rs.getTimestamp("deadline").toLocalDateTime());
                task.setProjectId(rs.getInt("project_id"));
                task.setCreatedBy(rs.getInt("created_by"));

                list.add(task);

            }

        } catch (SQLException e){
            throw new RuntimeException("getById task failed", e);
        }

        return list;
    }
    @Override
    public List<Task> getTasksBySearch(String title){
        List<Task> list = new ArrayList<>();

        String sql = "SELECT * FROM tasks WHERE title = ?";

        try(
                Connection connection = JDBCUtil.getConnection();
                PreparedStatement ps =connection.prepareStatement(sql);)
        {
            ps.setString(1, title);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                Task task = new Task();

                task.setId(rs.getInt("id"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setStatus(TaskStatus.valueOf(rs.getString("status")));
                task.setPriority(Priority.valueOf(rs.getString("priority")));
                task.setDeadline(rs.getTimestamp("deadline").toLocalDateTime());
                task.setProjectId(rs.getInt("project_id"));
                task.setCreatedBy(rs.getInt("created_by"));

                list.add(task);

            }

        } catch (SQLException e){
            throw new RuntimeException("getBySearch tasks failed", e);
        }

        return list;
    }

    @Override
    public List<Task> getUpcomingDeadlines() {
        List<Task> list = new ArrayList<>();

        String sql = "SELECT * FROM tasks WHERE DATEIFF(deadline, NOW()) <= 7 AND DATEIFF(deadline, NOW()) >= 0";

        try (
                Connection connection = JDBCUtil.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ){
            while(rs.next()){
                Task task = new Task();

                task.setId(rs.getInt("id"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setStatus(TaskStatus.valueOf(rs.getString("status")));
                task.setPriority(Priority.valueOf(rs.getString("priority")));
                task.setDeadline(rs.getTimestamp("deadline").toLocalDateTime());
                task.setProjectId(rs.getInt("project_id"));
                task.setCreatedBy(rs.getInt("created_by"));

                list.add(task);

            }

        } catch (SQLException e){
            throw new RuntimeException("getByDeadline tasks failed", e);
        }

        return list;
    }

    @Override
    public List<Task> getTasksByStatus(TaskStatus status){
        List<Task> list = new ArrayList<>();

        String sql = "SELECT * FROM tasks WHERE status = ?";

        try(
                Connection connection = JDBCUtil.getConnection();
                PreparedStatement ps =connection.prepareStatement(sql);)
        {
            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                Task task = new Task();

                task.setId(rs.getInt("id"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setStatus(TaskStatus.valueOf(rs.getString("status")));
                task.setPriority(Priority.valueOf(rs.getString("priority")));
                task.setDeadline(rs.getTimestamp("deadline").toLocalDateTime());
                task.setProjectId(rs.getInt("project_id"));
                task.setCreatedBy(rs.getInt("created_by"));

                list.add(task);

            }

        } catch (SQLException e){
            throw new RuntimeException("getByStatus tasks failed", e);
        }

        return list;
    }
    public List<Comment> getByTaskId(int taskId) {
        List<Comment> list = new ArrayList<>();

        String sql = "SELECT * FROM comments WHERE task_id = ? ORDER BY created_at DESC";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, taskId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Comment c = new Comment();
                c.setId(rs.getInt("id"));
                c.setTaskId(rs.getInt("task_id"));
                c.setUserName(rs.getString("user_name"));
                c.setContent(rs.getString("content"));
                c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                list.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void insert(Comment c) {
        String sql = "INSERT INTO comments(task_id, user_name, content) VALUES (?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, c.getTaskId());
            ps.setString(2, c.getUserName());
            ps.setString(3, c.getContent());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
