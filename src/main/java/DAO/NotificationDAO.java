package DAO;

import Enum.NotificationType;
import Model.Notification;
import Model.NotificationDTO;
import database.JDBCUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    // ================= COUNT ALL =================
    public int countAll(int userId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // ================= COUNT UNREAD =================
    public int countUnread(int userId) {
        String sql = """
            SELECT COUNT(*)
            FROM notifications
            WHERE user_id = ? AND is_read = false
        """;

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // ================= INSERT =================
    public boolean insert(Notification n) {
        String sql = """
            INSERT INTO notifications
            (user_id, project_id, task_id, type, is_read, created_by)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, n.getUserId());

            if (n.getProjectId() != null) ps.setInt(2, n.getProjectId());
            else ps.setNull(2, Types.INTEGER);

            if (n.getTaskId() != null) ps.setInt(3, n.getTaskId());
            else ps.setNull(3, Types.INTEGER);

            ps.setString(4, n.getType() != null ? n.getType().name() : null);
            ps.setBoolean(5, n.isRead());

            if (n.getCreatedBy() != null) ps.setInt(6, n.getCreatedBy());
            else ps.setNull(6, Types.INTEGER);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ================= GET BY USER =================
    public List<Notification> getByUserId(int userId) {
        String sql = """
            SELECT *
            FROM notifications
            WHERE user_id = ?
            ORDER BY created_at DESC
        """;

        List<Notification> list = new ArrayList<>();

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapNotification(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ================= GET UNREAD =================
    public List<Notification> getUnreadByUserId(int userId) {
        String sql = """
            SELECT *
            FROM notifications
            WHERE user_id = ? AND is_read = false
            ORDER BY created_at DESC
        """;

        List<Notification> list = new ArrayList<>();

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapNotification(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ================= GET BY TYPE =================
    public List<Notification> getByUserIdAndType(int userId, NotificationType type) {
        String sql = """
            SELECT *
            FROM notifications
            WHERE user_id = ? AND type = ?
            ORDER BY created_at DESC
        """;

        List<Notification> list = new ArrayList<>();

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, type.name());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapNotification(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ================= MARK =================
    public boolean markAsRead(int id) {
        String sql = "UPDATE notifications SET is_read = true WHERE id = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean markAllAsRead(int userId) {
        String sql = "UPDATE notifications SET is_read = true WHERE user_id = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ================= DELETE =================
    public boolean deleteById(int id) {
        String sql = "DELETE FROM notifications WHERE id = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ================= EXISTS DEADLINE =================
    public boolean existsDeadlineNotification(int userId, int taskId) {
        String sql = """
            SELECT COUNT(*)
            FROM notifications
            WHERE user_id = ? AND task_id = ? AND type = ?
        """;

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, taskId);
            ps.setString(3, NotificationType.DEADLINE.name());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ================= DTO QUERIES (CHUẨN HOÁ 1 FORMAT DUY NHẤT) =================
    public List<NotificationDTO> getNotificationDTOByUserId(int userId) {
        return getDTOBase(userId, null, false);
    }

    public List<NotificationDTO> getUnreadNotificationDTOByUserId(int userId) {
        return getDTOBase(userId, null, true);
    }

    public List<NotificationDTO> getNotificationDTOByUserIdAndType(int userId, NotificationType type) {
        return getDTOBase(userId, type, null);
    }

    // ================= CORE DTO QUERY (TRÁNH DUPLICATE SQL) =================
    private List<NotificationDTO> getDTOBase(int userId,
                                             NotificationType type,
                                             Boolean onlyUnread) {

        List<NotificationDTO> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT n.*,
                   p.name AS project_name,
                   t.title AS task_title,
                   u.name AS creator_name
            FROM notifications n
            LEFT JOIN projects p ON n.project_id = p.id
            LEFT JOIN tasks t ON n.task_id = t.id
            LEFT JOIN users u ON n.created_by = u.id
            WHERE n.user_id = ?
        """);

        if (onlyUnread != null && onlyUnread) {
            sql.append(" AND n.is_read = false");
        }

        if (type != null) {
            sql.append(" AND n.type = ?");
        }

        sql.append(" ORDER BY n.created_at DESC");

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            ps.setInt(1, userId);

            if (type != null) {
                ps.setString(2, type.name());
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapDTO(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ================= MAP NOTIFICATION =================
    private Notification mapNotification(ResultSet rs) throws SQLException {
        Notification n = new Notification();

        n.setId(rs.getInt("id"));
        n.setUserId(rs.getInt("user_id"));
        n.setProjectId((Integer) rs.getObject("project_id"));
        n.setTaskId((Integer) rs.getObject("task_id"));

        String type = rs.getString("type");
        if (type != null) n.setType(NotificationType.valueOf(type.trim()));

        n.setRead(rs.getBoolean("is_read"));
        n.setCreatedBy((Integer) rs.getObject("created_by"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) n.setCreatedAt(createdAt.toLocalDateTime());

        return n;
    }

    // ================= MAP DTO =================
    private NotificationDTO mapDTO(ResultSet rs) throws SQLException {
        NotificationDTO dto = new NotificationDTO();

        dto.setId(rs.getInt("id"));
        dto.setUserId(rs.getInt("user_id"));
        dto.setProjectId((Integer) rs.getObject("project_id"));
        dto.setTaskId((Integer) rs.getObject("task_id"));
        dto.setCreatedBy((Integer) rs.getObject("created_by"));

        dto.setProjectName(rs.getString("project_name"));
        dto.setTaskTitle(rs.getString("task_title"));
        dto.setCreatorName(rs.getString("creator_name"));

        String type = rs.getString("type");
        if (type != null) dto.setType(NotificationType.valueOf(type.trim()));

        dto.setRead(rs.getBoolean("is_read"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) dto.setCreatedAt(createdAt.toLocalDateTime());

        return dto;
    }
}