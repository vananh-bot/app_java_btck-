package DAO;

import Model.UserProject;
import Enum.Role;
import database.JDBCUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class UserProjectDAO implements UserProjectDAOInterface {

    // ➕ INSERT (thêm user vào project)
    @Override
    public boolean insert(UserProject up) {

        String sql = "INSERT INTO user_project(user_id, project_id, role) VALUES (?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, up.getUserId());                  // user_id
            ps.setInt(2, up.getProjectId());               // project_id
            ps.setString(3, up.getRole().name());          // enum → string (OWNER/MEMBER)

            return ps.executeUpdate() > 0;                 // true nếu thành công

        } catch (SQLIntegrityConstraintViolationException e) {
            // ❗ duplicate (do UNIQUE user_id + project_id)
            System.out.println("User đã ở trong project rồi!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // ❌ DELETE
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

    // 🔥 FIND BY PROJECT (list member)
    @Override
    public List<UserProject> findByProjectId(int projectId) {

        List<UserProject> list = new ArrayList<>();

        String sql = "SELECT * FROM user_project WHERE project_id=?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, projectId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapUserProject(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 🔥 FIND BY USER (dashboard + role)
    @Override
    public List<UserProject> findByUserId(int userId) {

        List<UserProject> list = new ArrayList<>();

        String sql = "SELECT * FROM user_project WHERE user_id=?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapUserProject(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 🔍 FIND ONE
    @Override
    public UserProject findOne(int userId, int projectId) {

        String sql = "SELECT * FROM user_project WHERE user_id=? AND project_id=?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, projectId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapUserProject(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 🔥 CHECK EXISTS
    @Override
    public boolean exists(int userId, int projectId) {

        String sql = "SELECT 1 FROM user_project WHERE user_id=? AND project_id=?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, projectId);

            ResultSet rs = ps.executeQuery();

            return rs.next(); // có record → true

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // 🔧 MAP ResultSet → UserProject
    private UserProject mapUserProject(ResultSet rs) throws SQLException {

        Timestamp ts = rs.getTimestamp("joined_at");

        LocalDateTime joinedAt = null;
        if (ts != null) {
            joinedAt = ts.toLocalDateTime(); // SQL → Java
        }

        return new UserProject(
                rs.getInt("user_id"),
                rs.getInt("project_id"),
                Role.valueOf(rs.getString("role")), // string → enum
                joinedAt
        );
    }
}