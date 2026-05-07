package DAO;

import database.JDBCUtil;
import java.sql.*;

    public class PasswordResetDAO {

        // Lưu mã mới và xóa các mã cũ của email này để dọn dẹp bộ nhớ
        public void insert(String email, String token) {
            // Xóa mã cũ của email này trước khi chèn mã mới
            deleteByEmail(email);

            String sql = "INSERT INTO password_resets (email, token, expires_at) VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 1 MINUTE))";
            try (Connection con = JDBCUtil.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, email);
                ps.setString(2, token);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Insert reset token failed", e);
            }
        }

        // Kiểm tra mã có khớp, chưa dùng và còn hạn không
        public boolean validateToken(String email, String token) {
            String sql = "SELECT id FROM password_resets WHERE email = ? AND token = ? AND is_used = 0 AND expires_at > NOW()";
            try (Connection con = JDBCUtil.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, email);
                ps.setString(2, token);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Validate token failed", e);
            }
        }

        // Xóa mã sau khi sử dụng thành công để "đỡ nặng" database
        public void deleteByEmail(String email) {
            String sql = "DELETE FROM password_resets WHERE email = ?";
            try (Connection con = JDBCUtil.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, email);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Delete token failed", e);
            }
        }
    }
