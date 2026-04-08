package DAO;

import Model.User;
import database.JDBCUtil;
import javafx.fxml.FXML;


import Model.Project;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class ProjectDAO implements ProjectDAOInterface {

    // 🔥 INSERT PROJECT (trả về id)
    @Override
    public int insert(Project project) {

        String sql = "INSERT INTO projects(name, description, owner_id, invite_code) VALUES (?, ?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, project.getName());         // name
            ps.setString(2, project.getDescription());  // description
            ps.setInt(3, project.getOwnerId());         // owner_id
            ps.setString(4, project.getInviteCode());   // invite_code

            int rows = ps.executeUpdate(); // chạy INSERT

            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys(); // lấy id vừa tạo
                if (rs.next()) {
                    return rs.getInt(1); // trả về id
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1; // lỗi
    }

    // ✏️ UPDATE PROJECT
    @Override
    public boolean update(Project project) {

        String sql = "UPDATE projects SET name=?, description=? WHERE id=?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, project.getName());
            ps.setString(2, project.getDescription());
            ps.setInt(3, project.getId());

            return ps.executeUpdate() > 0; // trả true nếu update thành công

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // ❌ DELETE PROJECT
    @Override
    public boolean delete(int id) {

        String sql = "DELETE FROM projects WHERE id=?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // 🔍 FIND BY ID
    @Override
    public Project findById(int id) {

        String sql = "SELECT * FROM projects WHERE id=?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapProject(rs); // dùng hàm map
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 📋 FIND ALL
    @Override
    public List<Project> findAll() {

        List<Project> list = new ArrayList<>();

        String sql = "SELECT * FROM projects";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapProject(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 🔥 LOAD PROJECT THEO USER (dashboard)
    @Override
    public List<Project> findByUserId(int userId) {

        List<Project> list = new ArrayList<>();

        String sql = """
            SELECT p.*
            FROM projects p
            JOIN user_project up ON p.id = up.project_id
            WHERE up.user_id = ?
        """;

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapProject(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 🔥 FIND BY INVITE CODE
    @Override
    public Project findByInviteCode(String code) {

        String sql = "SELECT * FROM projects WHERE invite_code=?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapProject(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 🔧 HÀM MAP CHUNG (cực quan trọng)
    private Project mapProject(ResultSet rs) throws SQLException {

        Timestamp ts = rs.getTimestamp("created_at");

        LocalDateTime createdAt = null;
        if (ts != null) {
            createdAt = ts.toLocalDateTime(); // convert SQL → Java
        }

        Project p = new Project(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getInt("owner_id"),     // 🔥 DB → Java
                rs.getString("invite_code"),
                createdAt
        );

        return p;
    }
    public Project getProjectWithMembers(int projectId) {

        String sql = """
        SELECT 
            p.id as p_id,
            p.name as p_name,
            p.description as p_desc,
            p.owner_id,
            p.invite_code,
            p.created_at as p_created,

            u.id as u_id,
            u.email,
            u.password,
            u.name,
            u.is_verified,
            u.created_at as u_created,
            u.updated_at as u_updated

        FROM projects p
        JOIN user_project up ON p.id = up.project_id
        JOIN users u ON u.id = up.user_id
        WHERE p.id = ?
    """;

        Project project = null;
        List<User> members = new ArrayList<>();

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, projectId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                // 🔥 tạo project 1 lần
                if (project == null) {

                    Timestamp ts = rs.getTimestamp("p_created");
                    LocalDateTime createdAt = (ts != null) ? ts.toLocalDateTime() : null;

                    project = new Project(
                            rs.getInt("p_id"),
                            rs.getString("p_name"),
                            rs.getString("p_desc"),
                            rs.getInt("owner_id"),
                            rs.getString("invite_code"),
                            createdAt
                    );
                }

                // 🔥 xử lý null time cho user
                Timestamp uCreatedTs = rs.getTimestamp("u_created");
                LocalDateTime uCreated = (uCreatedTs != null) ? uCreatedTs.toLocalDateTime() : null;

                Timestamp uUpdatedTs = rs.getTimestamp("u_updated");
                LocalDateTime uUpdated = (uUpdatedTs != null) ? uUpdatedTs.toLocalDateTime() : null;

                // 🔥 tạo user
                User user = new User(
                        rs.getInt("u_id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getBoolean("is_verified"),
                        uCreated,
                        uUpdated
                );

                members.add(user);
            }

            // 🔥 gán members
            if (project != null) {
                project.setMembers(members);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return project;
    }
}
