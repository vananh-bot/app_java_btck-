package DAO;


import Model.Task;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import Model.TaskDashboardDTO;
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

    public List<TaskDashboardDTO> getDashboardMyTask(int userId){
        List<TaskDashboardDTO> list = new ArrayList<>();

        String sql = "SELECT \n" +
                "    t.id,\n" +
                "    t.title,\n" +
                "    t.priority,\n" +
                "    t.deadline\n" +
                "\n" +
                "FROM tasks t\n" +
                "JOIN projects p ON t.project_id = p.id\n" +
                "\n" +
                "WHERE p.owner_id = ?\n" +
                "  AND t.status <> 'DONE'\n" +
                "\n" +
                "ORDER BY\n" +
                "(\n" +
                "    CASE t.status\n" +
                "        WHEN 'IN_PROGRESS' THEN 2\n" +
                "        WHEN 'TODO' THEN 1\n" +
                "        ELSE 0\n" +
                "    END\n" +
                "    +\n" +
                "    CASE t.priority\n" +
                "        WHEN 'HIGH' THEN 3\n" +
                "        WHEN 'MEDIUM' THEN 2\n" +
                "        WHEN 'LOW' THEN 1\n" +
                "    END\n" +
                "    +\n" +
                "    CASE \n" +
                "        WHEN t.deadline < NOW() THEN 5\n" +
                "        WHEN t.deadline < DATE_ADD(NOW(), INTERVAL 1 DAY) THEN 4\n" +
                "        WHEN t.deadline < DATE_ADD(NOW(), INTERVAL 3 DAY) THEN 3\n" +
                "        WHEN t.deadline < DATE_ADD(NOW(), INTERVAL 7 DAY) THEN 2\n" +
                "        ELSE 1\n" +
                "    END\n" +
                ") DESC,\n" +
                "t.deadline ASC;";

        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
        ){
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                int id = rs.getInt("id");
                String title = rs.getString("title");
                Priority priority = Priority.valueOf(rs.getString("priority"));
                Timestamp ts = rs.getTimestamp("deadline");
                LocalDateTime deadline = ts != null ? ts.toLocalDateTime() : null;

                TaskDashboardDTO task = new TaskDashboardDTO(id, title, priority, deadline);
                list.add(task);
            }

        } catch (SQLException e) {
            throw new RuntimeException("getDashboardMyTask failed");
        }

        return list;
    }



}