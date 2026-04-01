package DAO;

import Model.User;
import database.JDBCUtil;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class UserDAO implements UserInterfaceDao<User> {
    public boolean checkLogin(String name, String password) {
        boolean isValid = false;
     //   String sql = "SELECT * FROM users WHERE name = ? AND password = ?";
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
            e.printStackTrace();
        }
        return isValid;
    }

    @Override
    public int insert(User user) {
        String sql = "INSERT INTO users(email, password, name, is_verified) VALUES (?, ?, ?, ?)";

        try (
                Connection con = JDBCUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getName());
            ps.setBoolean(4, user.isVerified());

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Insert user failed", e);
        }
    }

    @Override
    public int update(User user) {
        String sql = "UPDATE users SET name=?, is_verified=? WHERE id=?";

        try (
                Connection con = JDBCUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, user.getName());
            ps.setBoolean(2, user.isVerified());
            ps.setInt(3, user.getId());

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Update user failed", e);
        }
    }

    @Override
    public int deleteById(int id) {
        String sql = "DELETE FROM users WHERE id=?";

        try (
                Connection con = JDBCUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Delete user failed", e);
        }
    }
    @Override
    public List<User> selectAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (
                Connection con = JDBCUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Find all users failed", e);
        }

        return list;
    }

    @Override
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id=?";

        try (
                Connection con = JDBCUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Find user by id failed", e);
        }

        return null;
    }

    @Override
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email=?";

        try (
                Connection con = JDBCUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Find user by email failed", e);
        }

        return null;
    }

    @Override
    public User findByName(String name) {
        String sql = "SELECT * FROM users WHERE name=?";

        try (
                Connection con = JDBCUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Find user by name failed", e);
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