package DAO;

import Model.User;
import Model.Project;
import database.JDBCUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Model.ProjectDashboardDTO;

public class ProjectDAO implements ProjectDAOInterface {

    @Override
    public int insert(Project project) {
        String sql = "INSERT INTO projects(name, description, owner_id, invite_code) VALUES (?, ?, ?, ?)";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, project.getName());
            ps.setString(2, project.getDescription());
            ps.setInt(3, project.getOwnerId());
            ps.setString(4, project.getInviteCode());

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean update(Project project) {
        String sql = "UPDATE projects SET name=?, description=? WHERE id=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, project.getName());
            ps.setString(2, project.getDescription());
            ps.setInt(3, project.getId());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

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

    @Override
    public Project findById(int id) {
        String sql = "SELECT * FROM projects WHERE id=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapProject(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

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

    @Override
    public List<Project> findByUserId(int userId) {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT p.* FROM projects p JOIN user_project up ON p.id = up.project_id WHERE up.user_id = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapProject(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Project findByInviteCode(String code) {
        String sql = "SELECT * FROM projects WHERE invite_code=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapProject(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Project mapProject(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("created_at");
        LocalDateTime createdAt = (ts != null) ? ts.toLocalDateTime() : null;

        return new Project(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getInt("owner_id"),
                rs.getString("invite_code"),
                createdAt
        );
    }

    public Project getProjectWithMembers(int projectId) {
        // Đã sửa thành LEFT JOIN để không bị mất Project nếu chưa có ai
        String sql = """
            SELECT 
                p.id as p_id, p.name as p_name, p.description as p_desc,
                p.owner_id, p.invite_code, p.created_at as p_created,
                u.id as u_id, u.email, u.password, u.name, u.is_verified,
                u.created_at as u_created, u.updated_at as u_updated
            FROM projects p
            LEFT JOIN user_project up ON p.id = up.project_id
            LEFT JOIN users u ON u.id = up.user_id
            WHERE p.id = ?
        """;

        Project project = null;
        List<User> members = new ArrayList<>();

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (project == null) {
                        Timestamp ts = rs.getTimestamp("p_created");
                        LocalDateTime createdAt = (ts != null) ? ts.toLocalDateTime() : null;

                        project = new Project(
                                rs.getInt("p_id"), rs.getString("p_name"), rs.getString("p_desc"),
                                rs.getInt("owner_id"), rs.getString("invite_code"), createdAt
                        );
                    }

                    int userId = rs.getInt("u_id");
                    if (!rs.wasNull()) { // Bắt buộc check null vì dùng LEFT JOIN
                        Timestamp uCreatedTs = rs.getTimestamp("u_created");
                        LocalDateTime uCreated = (uCreatedTs != null) ? uCreatedTs.toLocalDateTime() : null;

                        Timestamp uUpdatedTs = rs.getTimestamp("u_updated");
                        LocalDateTime uUpdated = (uUpdatedTs != null) ? uUpdatedTs.toLocalDateTime() : null;

                        User user = new User(
                                userId, rs.getString("email"), rs.getString("password"),
                                rs.getString("name"), rs.getBoolean("is_verified"), uCreated, uUpdated
                        );
                        members.add(user);
                    }
                }
                if (project != null) {
                    project.setMembers(members);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return project;
    }

    public boolean isProjectNameExists(int userId, String projectName) {
        String sql = "SELECT 1 FROM projects p " +
                "JOIN user_project up ON p.id = up.project_id " +
                "WHERE up.user_id = ? AND LOWER(p.name) = LOWER(?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, projectName);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<ProjectDashboardDTO> getDashboardProject(int userId){
        List<ProjectDashboardDTO> projects = new ArrayList<>();

        String sql = "SELECT \n" +
                "    p.id,\n" +
                "    p.name,\n" +
                "\n" +
                "    SUM(CASE WHEN t.status = 'TODO' THEN 1 ELSE 0 END) AS toDoCount,\n" +
                "    SUM(CASE WHEN t.status = 'IN_PROGRESS' THEN 1 ELSE 0 END) AS inProgressCount,\n" +
                "    SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END) AS doneCount\n" +
                "\n" +
                "FROM projects p\n" +
                "LEFT JOIN tasks t ON p.id = t.project_id\n" +
                "\n" +
                "WHERE p.owner_id = ?\n" +
                "\n" +
                "GROUP BY p.id, p.name\n" +
                "\n" +
                "ORDER BY\n" +
                "SUM(\n" +
                "    CASE \n" +
                "        WHEN t.status = 'DONE' THEN 0\n" +
                "        WHEN t.status = 'IN_PROGRESS' THEN\n" +
                "            (CASE t.priority \n" +
                "                WHEN 'HIGH' THEN 3\n" +
                "                WHEN 'MEDIUM' THEN 2\n" +
                "                WHEN 'LOW' THEN 1\n" +
                "            END) * 2\n" +
                "        WHEN t.status = 'TODO' THEN\n" +
                "            (CASE t.priority \n" +
                "                WHEN 'HIGH' THEN 3\n" +
                "                WHEN 'MEDIUM' THEN 2\n" +
                "                WHEN 'LOW' THEN 1\n" +
                "            END)\n" +
                "    END\n" +
                ") DESC\n" +
                "\n" +
                "LIMIT 20;";

        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);){

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int toDoCount = rs.getInt("toDoCount");
                int inProgressCount = rs.getInt("inProgressCount");
                int doneCount = rs.getInt("doneCount");

                ProjectDashboardDTO project = new ProjectDashboardDTO(id, name, toDoCount, inProgressCount, doneCount);

                projects.add(project);
            }


        } catch (SQLException e) {
            throw new RuntimeException("getDashboardProject failed");
        }
        return projects;
    }
}
