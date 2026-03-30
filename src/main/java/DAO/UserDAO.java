package DAO;

import DAO.UserInterfaceDao;
import Model.User;
import database.JDBCUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements UserInterfaceDao<User> {

    private static final UserDAO instance = new UserDAO();
    public static UserDAO getInstance() {
        return instance;
    }

    private UserDAO() {}


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
            e.printStackTrace();
        }
        return 0;
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
            e.printStackTrace();
        }
        return 0;
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
            e.printStackTrace();
        }
        return 0;
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
            e.printStackTrace();
        }
        return list;
    }


    @Override
    public User selectById(int id) {
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
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User selectByEmail(String email) {
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
            e.printStackTrace();
        }
        return null;
    }

    public User login(String email, String password) {
        User user = selectByEmail(email);

        if (user != null && user.checkPassword(password)) {
            return user;
        }
        return null;
    }

    private User mapResultSet(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("name"),
                rs.getBoolean("is_verified"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime()
        );
    }
}