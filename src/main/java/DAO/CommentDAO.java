package DAO;

import Model.Comment;
import database.JDBCUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO implements CommentDAOInterface{
    // =========================
    // GET COMMENTS BY TASK ID
    // =========================
    public List<Comment> getByTaskId(int taskId) {

        List<Comment> list = new ArrayList<>();

        String sql = """
            SELECT c.id,
                   c.task_id,
                   c.user_id,
                   u.name AS user_name,
                   c.content,
                   c.created_at
            FROM comments c
            JOIN users u ON c.user_id = u.id
            WHERE c.task_id = ?
            ORDER BY c.created_at DESC
        """;

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, taskId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Comment c = new Comment();

                c.setId(rs.getInt("id"));
                c.setTaskId(rs.getInt("task_id"));
                c.setUserId(rs.getInt("user_id"));
                c.setUserName(rs.getString("user_name")); // lấy từ users table
                c.setContent(rs.getString("content"));

                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) {
                    c.setCreatedAt(ts.toLocalDateTime());
                }

                list.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // =========================
    // INSERT COMMENT
    // =========================
    public boolean insert(Comment c) {

        String sql = """
            INSERT INTO comments (task_id, user_id, content)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, c.getTaskId());
            ps.setInt(2, c.getUserId());
            ps.setString(3, c.getContent());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================
    // DELETE COMMENT
    // =========================
    public boolean deleteById(int commentId) {

        String sql = "DELETE FROM comments WHERE id = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, commentId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================
    // UPDATE COMMENT
    // =========================
    public boolean update(Comment c) {

        String sql = """
            UPDATE comments
            SET content = ?
            WHERE id = ?
        """;

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getContent());
            ps.setInt(2, c.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================
    // COUNT COMMENTS BY TASK
    // =========================
    public int countByTaskId(int taskId) {

        String sql = "SELECT COUNT(*) FROM comments WHERE task_id = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, taskId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
