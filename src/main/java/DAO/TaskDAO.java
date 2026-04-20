package DAO;

import Model.*;
import database.JDBCUtil;
import Enum.TaskStatus;
import Enum.Priority;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO implements TaskInterfaceDAO<Task> {

    // ================= INSERT =================
    @Override
    public int insert(Task task) {
        String sql = "INSERT INTO tasks (title, description, status, priority, deadline, project_id, created_by) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getStatus().name());
            ps.setString(4, task.getPriority().name());

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
    public int insert2(Task task) {
        String sql = "INSERT INTO tasks (title, description, status, priority, deadline, project_id) VALUES (?, ?, ?, ?, ?, ?)";
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

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Insert task failed", e);
        }
    }

    // ================= VALIDATE =================
    public boolean existsByTitleAndProject(String title, int projectId) {
        String sql = "SELECT 1 FROM tasks WHERE title = ? AND project_id = ? LIMIT 1";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, title.trim());
            ps.setInt(2, projectId);

            return ps.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ================= DELETE =================
    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Delete failed", e);
        }
    }

    // ================= UPDATE =================
    @Override
    public int update(Task task) {
        String sql = "UPDATE tasks SET title = ?, description = ?, status = ?, priority = ?, deadline = ? WHERE id = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

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
            throw new RuntimeException("Update failed", e);
        }
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

    // ================= GET =================
    @Override
    public Task getById(int id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapTask(rs);

        } catch (SQLException e) {
            throw new RuntimeException("getById failed", e);
        }
        return null;
    }

    @Override
    public List<Task> getTasksByProjectId(int projectId) {
        return getList("SELECT * FROM tasks WHERE project_id = ?", projectId);
    }

    @Override
    public List<Task> getTasksByPriority(Priority priority) {
        return getList("SELECT * FROM tasks WHERE priority = ?", priority.name());
    }

    @Override
    public List<Task> getTasksBySearch(String title) {
        return getList("SELECT * FROM tasks WHERE title LIKE ?", "%" + title + "%");
    }

    @Override
    public List<Task> getTasksByStatus(TaskStatus status) {
        return getList("SELECT * FROM tasks WHERE status = ?", status.name());
    }

    @Override
    public List<Task> getUpcomingDeadlines() {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE DATEDIFF(deadline, NOW()) BETWEEN 0 AND 7";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapTask(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Deadline query failed", e);
        }

        return list;
    }

    // ================= COMMENT =================
    public List<Comment> getCommentsByTaskId(int taskId) {
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

    public void insertComment(Comment c) {
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

    // ================= HELPER =================
    private Task mapTask(ResultSet rs) throws SQLException {
        Task t = new Task();

        t.setId(rs.getInt("id"));
        t.setTitle(rs.getString("title"));
        t.setDescription(rs.getString("description"));
        t.setStatus(TaskStatus.valueOf(rs.getString("status")));
        t.setPriority(Priority.valueOf(rs.getString("priority")));

        Timestamp deadlineTs = rs.getTimestamp("deadline");
        t.setDeadline(deadlineTs != null ? deadlineTs.toLocalDateTime() : null);

        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            t.setCreatedAt(createdTs.toLocalDateTime());
        }

        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) {
            t.setUpdatedAt(updatedTs.toLocalDateTime());
        }

        t.setProjectId(rs.getInt("project_id"));
        t.setCreatedBy(rs.getInt("created_by"));

        return t;
    }

    private List<Task> getList(String sql, Object param) {
        List<Task> list = new ArrayList<>();

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (param instanceof String) ps.setString(1, (String) param);
            else ps.setInt(1, (Integer) param);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) list.add(mapTask(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Query failed", e);
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