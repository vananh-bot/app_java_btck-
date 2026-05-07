package DAO;


import java.sql.*;
import java.util.*;

import Model.JoinRequest;
import database.JDBCUtil;
import Enum.RequestStatus;

public class JoinRequestDAO implements JoinRequestDAOInterface {

    @Override
    public void create(Connection conn, JoinRequest r) {
        String sql = "INSERT INTO Join_Request(project_id, user_id, status) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getProjectId());
            ps.setInt(2, r.getUserId());
            ps.setString(3, r.getStatus().name());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<JoinRequest> findByProject(int projectId) {
        List<JoinRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM Join_Request WHERE project_id = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, projectId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) list.add(map(rs));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<JoinRequest> findByUser(int userId) {
        List<JoinRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM Join_Request WHERE user_id = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) list.add(map(rs));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public JoinRequest findById(Connection conn, int id) {
        String sql = "SELECT * FROM Join_Request WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return map(rs);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void updateStatus(Connection conn, int id, RequestStatus status) {
        String sql = "UPDATE Join_Request SET status=? WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private JoinRequest map(ResultSet rs) throws SQLException {
        JoinRequest r = new JoinRequest();
        r.setId(rs.getInt("id"));
        r.setProjectId(rs.getInt("project_id"));
        r.setUserId(rs.getInt("user_id"));
        r.setStatus(RequestStatus.valueOf(rs.getString("status")));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        return r;
    }
}
