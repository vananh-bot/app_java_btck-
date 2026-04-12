package DAO;

import Model.UserProject;
import Enum.Role;
import database.JDBCUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class UserProjectDAO implements UserProjectDAOInterface {

    @Override
    public boolean insert(UserProject up) {
        String sql = "INSERT INTO user_project(user_id, project_id, role) VALUES (?, ?, ?)";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, up.getUserId());
            ps.setInt(2, up.getProjectId());
            ps.setString(3, up.getRole().name());

            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("User đã ở trong project rồi!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int userId, int projectId) {
        String sql = "DELETE FROM user_project WHERE user_id=? AND project_id=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, projectId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<UserProject> findByProjectId(int projectId) {
        List<UserProject> list = new ArrayList<>();
        String sql = "SELECT * FROM user_project WHERE project_id=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapUserProject(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<UserProject> findByUserId(int userId) {
        List<UserProject> list = new ArrayList<>();
        String sql = "SELECT * FROM user_project WHERE user_id=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapUserProject(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public UserProject findOne(int userId, int projectId) {
        String sql = "SELECT * FROM user_project WHERE user_id=? AND project_id=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUserProject(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean exists(int userId, int projectId) {
        String sql = "SELECT 1 FROM user_project WHERE user_id=? AND project_id=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private UserProject mapUserProject(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("joined_at");
        LocalDateTime joinedAt = (ts != null) ? ts.toLocalDateTime() : null;

        return new UserProject(
                rs.getInt("user_id"),
                rs.getInt("project_id"),
                Role.valueOf(rs.getString("role")),
                joinedAt
        );
    }
}