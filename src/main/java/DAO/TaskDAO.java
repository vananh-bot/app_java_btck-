package DAO;

import Model.Task;
import database.JDBCUtil;
import Enum.TaskStatus;
import Enum.Priority;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO implements TaskInterfaceDAO<Task> {

    @Override
    public int insert(Task task) {
        String sql = "INSERT INTO tasks (title, description, status, priority, deadline, project_id, created_by) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getStatus().name());
            ps.setString(4, task.getPriority().name());

            // Fix NullPointerException nếu Task không có deadline khi tạo
            if (task.getDeadline() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(task.getDeadline()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }

            ps.setInt(6, task.getProjectId());
            ps.setInt(7, task.getCreatedBy());

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Insert task failed", e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Delete task failed", e);
        }
    }

    @Override
    public int update(Task task) {
        String sql = "UPDATE tasks SET title = ?, description = ?, status = ?, priority = ?, deadline = ? WHERE id = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getStatus().name());
            ps.setString(4, task.getPriority().name());

            if (task.getDeadline() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(task.getDeadline()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }

            ps.setInt(6, task.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Update task failed", e);
        }
    }

    @Override
    public Task getById(int id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapTask(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("getById task failed", e);
        }
        return null;
    }

    @Override
    public List<Task> getTasksByProjectId(int projectId) {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE project_id = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTask(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("GetTasksByIdProject failed", e);
        }
        return list;
    }

    @Override
    public List<Task> getTasksByPriority(Priority priority) {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE priority = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, priority.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTask(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("getTasksByPriority failed", e);
        }
        return list;
    }

    @Override
    public List<Task> getTasksBySearch(String title) {
        List<Task> list = new ArrayList<>();
        // Đã sửa thành LIKE để tìm kiếm tương đối
        String sql = "SELECT * FROM tasks WHERE title LIKE ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, "%" + title + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTask(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("getBySearch tasks failed", e);
        }
        return list;
    }

    @Override
    public List<Task> getUpcomingDeadlines() {
        List<Task> list = new ArrayList<>();
        // Đã sửa lỗi chính tả DATEDIFF
        String sql = "SELECT * FROM tasks WHERE DATEDIFF(deadline, NOW()) <= 7 AND DATEDIFF(deadline, NOW()) >= 0";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapTask(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("getByDeadline tasks failed", e);
        }
        return list;
    }

    @Override
    public List<Task> getTasksByStatus(TaskStatus status) {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE status = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTask(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("getByStatus tasks failed", e);
        }
        return list;
    }

    public void updateStatus(int taskId, TaskStatus status) {
        String sql = "UPDATE tasks SET status = ? WHERE id = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setInt(2, taskId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Hàm phụ trợ dùng chung giúp loại bỏ NullPointerException cho Deadline
    private Task mapTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getInt("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setStatus(TaskStatus.valueOf(rs.getString("status")));
        task.setPriority(Priority.valueOf(rs.getString("priority")));

        Timestamp deadlineTs = rs.getTimestamp("deadline");
        task.setDeadline((deadlineTs != null) ? deadlineTs.toLocalDateTime() : null);

        task.setProjectId(rs.getInt("project_id"));
        task.setCreatedBy(rs.getInt("created_by"));
        return task;
    }
}