package DAO;
import Model.Notification;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Enum.NotificationType;
public class NotificationDAO {
    public List<Notification> getNotificationsByUserId(int userId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notification WHERE user_id = ? ORDER BY create_at DESC";
        try (Connection con = database.JDBCUtil.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, userId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Notification n = new Notification();
                n.setId(rs.getInt("id"));
                n.setUserId(rs.getInt("user_id"));
                n.setContent(rs.getString("content"));
                try {
                    String typeValue = rs.getString("type");
                    if (typeValue != null) {
                        n.setType(String.valueOf(NotificationType.valueOf(typeValue.toUpperCase())));
                    }
                } catch (IllegalArgumentException e) {
                    n.setType(String.valueOf(NotificationType.TASK_ASSIGNED));
                }
                n.setRead(rs.getBoolean("is_read"));
                n.setCreatedAt(rs.getTimestamp("create_at").toLocalDateTime());
                list.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE notification SET is_read = 1 WHERE id = ?";
        try (Connection con = database.JDBCUtil.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setInt(1, notificationId);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean insert(Notification n) {
        String sql = "INSERT INTO notification (user_id, content, type, is_read, target_id, target_type) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = database.JDBCUtil.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setInt(1, n.getUserId());
            st.setString(2, n.getContent());
            st.setString(3, n.getType());
            st.setBoolean(4, n.isRead());
            st.setInt(5, n.getTargetId());
            st.setString(6, n.getTargetType());

            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
