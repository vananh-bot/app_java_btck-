package DAO;

import DTO.TaskDashboardDTO;
import Model.*;
import Utils.AppErrorHandler;
import database.JDBCUtil;
import Enum.TaskStatus;
import Enum.Priority;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO implements TaskInterfaceDAO<Task> {

    // ================= INSERT =================
    public int insert(Task task) {
        String sql = "INSERT INTO tasks (title, description, status, priority, deadline, project_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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
            ps.executeUpdate();

            // lay id task vua tao
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    task.setId(newId);
                    return newId;
                }
            }
            return -1;
        } catch (SQLException e) {
            AppErrorHandler.handle(e);
            return -1; // Lỗi mạng / DB thì trả về -1
        }
    }

    // ================= VALIDATE =================
    public boolean existsByTitleAndProject(String title, int projectId) {
        String sql = "SELECT 1 FROM tasks WHERE title = ? AND project_id = ? LIMIT 1";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, title.trim());
            ps.setInt(2, projectId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            AppErrorHandler.handle(e);
            return false;
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
            AppErrorHandler.handle(e);
            return false;
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
            AppErrorHandler.handle(e);
            return 0;
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
            AppErrorHandler.handle(e);
        }
    }

    // ================= GET =================
    @Override
    public Task getById(int id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapTask(rs);
                }
            }

        } catch (SQLException e) {
            AppErrorHandler.handle(e);
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
            AppErrorHandler.handle(e);
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
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Comment c = new Comment();
                    c.setId(rs.getInt("id"));
                    c.setTaskId(rs.getInt("task_id"));
                    c.setUserName(rs.getString("user_name"));
                    c.setContent(rs.getString("content"));
                    c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    list.add(c);
                }
            }

        } catch (Exception e) {
            AppErrorHandler.handle(e);
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
            AppErrorHandler.handle(e);
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

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTask(rs));
                }
            }

        } catch (SQLException e) {
            AppErrorHandler.handle(e);
        }

        return list;
    }

    public List<TaskDashboardDTO> getDashboardMyTask(int userId){
        List<TaskDashboardDTO> list = new ArrayList<>();

        String sql = "SELECT \n" +
                "    t.id,\n" +
                "    t.title,\n" +
                "    t.priority,\n" +
                "    t.status,\n" +
                "    t.deadline,\n" +
                "    p.name AS project_name" +
                "\n" +
                "FROM tasks t\n" +
                "JOIN projects p ON t.project_id = p.id\n" +
                "JOIN user_project up ON p.id = up.project_id\n" +
                "\n" +
                "WHERE up.user_id = ?\n" +
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
             PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()){
                    int id = rs.getInt("id");
                    String title = rs.getString("title");
                    TaskStatus status = TaskStatus.valueOf(rs.getString("status"));
                    String projectName = rs.getString("project_name");
                    Priority priority = Priority.valueOf(rs.getString("priority"));
                    Timestamp ts = rs.getTimestamp("deadline");
                    LocalDateTime deadline = ts != null ? ts.toLocalDateTime() : null;

                    TaskDashboardDTO task = new TaskDashboardDTO(id, title, projectName, status, priority, deadline);
                    list.add(task);
                }
            }

        } catch (SQLException e) {
            AppErrorHandler.handle(e);
        }

        return list;
    }


    public Integer getProjectIdByTaskId(int taskId) {
        String sql = "SELECT project_id FROM tasks WHERE id = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("project_id");
                }
            }

        } catch (SQLException e) {
            AppErrorHandler.handle(e);
        }

        return null;
    }

    public List<Task> getAllTasks() {
        List<Task> list = new ArrayList<>();

        String sql = "SELECT * FROM tasks";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapTask(rs));
            }

        } catch (SQLException e) {
            AppErrorHandler.handle(e);
        }

        return list;
    }

    public List<String> getEmailsInProject(int projectId) {
        List<String> emails = new ArrayList<>();
        String sql = "SELECT u.email FROM users u JOIN user_project up ON u.id = up.user_id WHERE up.project_id = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String email = rs.getString("email");
                    if (email != null) emails.add(email);
                }
            }
        } catch (SQLException e) {
            AppErrorHandler.handle(e);
        }
        return emails;
    }

    public String getProjectNameById(int projectId) {
        String sql = "SELECT name FROM projects WHERE id = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, projectId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            AppErrorHandler.handle(e);
        }
        return "Dự án chung (FlowTask)";
    }

    public String getEmailByUserId(int userId) {
        String sql = "SELECT email FROM users WHERE id = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("email");
                }
            }
        } catch (SQLException e) {
            AppErrorHandler.handle(e);
        }
        return null;
    }

    public List<TaskDashboardDTO> getTasksForOverdueEmail() {
        List<TaskDashboardDTO> list = new ArrayList<>();
        String sql = """
        SELECT t.id, t.title, p.name AS project_name, u.email, t.deadline
        FROM tasks t
        JOIN projects p ON t.project_id = p.id
        JOIN user_project up ON p.id = up.project_id
        JOIN users u ON up.user_id = u.id
        WHERE t.deadline < NOW() 
          AND t.status <> 'DONE' 
          AND t.overdue_notified = 0
        """;

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TaskDashboardDTO dto = new TaskDashboardDTO(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("project_name"),
                            null,
                            null,
                            rs.getTimestamp("deadline").toLocalDateTime()
                    );
                    dto.setUserEmail(rs.getString("email"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            AppErrorHandler.handle(e);
        }
        return list;
    }

    public void markAsOverdueNotified(int taskId) {
        String sql = "UPDATE tasks SET overdue_notified = 1 WHERE id = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, taskId);
            ps.executeUpdate();

        } catch (SQLException e) {
            AppErrorHandler.handle(e);
        }
    }
}