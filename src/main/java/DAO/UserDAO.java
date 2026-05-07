package DAO;

import Model.User;
import Utils.AppErrorHandler;
import database.JDBCUtil;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import Enum.Role;

public class UserDAO implements UserInterfaceDao<User> {

    public boolean checkLogin(String name, String password) {
        boolean isValid = false;
        String sql = "SELECT * FROM users WHERE BINARY name = ? AND BINARY password = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, name);
            st.setString(2, password);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    isValid = true;
                }
            }
        } catch (SQLException e) {
            AppErrorHandler.handle(e);
        }
        return isValid;
    }

    @Override
    public int insert(User user) {
        String sql = "INSERT INTO users(email, password, name, is_verified) VALUES (?, ?, ?, ?)";

        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getName());
            ps.setBoolean(4, user.isVerified());

            return ps.executeUpdate();

        } catch (SQLException e) {
            AppErrorHandler.handle(e);
            return 0; // Trả về 0 (không có dòng nào được thêm) khi có lỗi
        }
    }

    @Override
    public int update(User user) {
        String sql = "UPDATE users SET name=?, is_verified=? WHERE id=?";

        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setBoolean(2, user.isVerified());
            ps.setInt(3, user.getId());

            return ps.executeUpdate();

        } catch (SQLException e) {
            AppErrorHandler.handle(e);
            return 0;
        }
    }

    @Override
    public int deleteById(int id) {
        String sql = "DELETE FROM users WHERE id=?";

        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate();

        } catch (SQLException e) {
            AppErrorHandler.handle(e);
            return 0;
        }
    }

    @Override
    public List<User> selectAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            AppErrorHandler.handle(e);
            // Lỗi thì trả về danh sách rỗng, tránh bị NullPointerException ở các lớp UI
        }

        return list;
    }

    @Override
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id=?";

        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }

        } catch (SQLException e) {
            AppErrorHandler.handle(e);
        }

        return null; // Trả về null nếu có lỗi hoặc không tìm thấy
    }

    @Override
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email=?";

        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }

        } catch (SQLException e) {
            AppErrorHandler.handle(e);
        }

        return null;
    }

    @Override
    public User findByName(String name) {
        String sql = "SELECT * FROM users WHERE name=?";

        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }

        } catch (SQLException e) {
            AppErrorHandler.handle(e);
        }

        return null;
    }

    public User login(String email, String password) {
        User user = findByEmail(email);

        if (user != null && user.checkPassword(password)) {
            return user;
        }
        return null;
    }

    public boolean updatePassword(String email, String newPassword) {
        String sql = "UPDATE users SET password=? WHERE email=?";

        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, email);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            AppErrorHandler.handle(e);
            return false; // Trả về false khi có lỗi mạng/DB
        }
    }

    private User mapResultSet(ResultSet rs) throws SQLException {
        Timestamp createdTs = rs.getTimestamp("created_at");
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        LocalDateTime createdAt = (createdTs != null) ? createdTs.toLocalDateTime() : null;
        LocalDateTime updatedAt = (updatedTs != null) ? updatedTs.toLocalDateTime() : null;

        return new User(
                rs.getInt("id"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("name"),
                rs.getBoolean("is_verified"),
                createdAt,
                updatedAt
        );
    }
}