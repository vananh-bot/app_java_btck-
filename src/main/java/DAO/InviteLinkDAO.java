package DAO;
import DAO.InviteLinkDAOInterface;
import Model.InviteLink;

import Model.User;
import Model.Project;
import database.JDBCUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import Enum.JoinMode;

public class InviteLinkDAO implements InviteLinkDAOInterface {

    @Override
    public void create(InviteLink link) {
        String sql = "INSERT INTO Invite_Link(project_id, token, join_mode, is_active) VALUES (?, ?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, link.getProjectId());
            ps.setString(2, link.getToken());
            ps.setString(3, link.getJoinMode().name());
            ps.setBoolean(4, link.isActive());

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public InviteLink findByToken(String token) {
        String sql = "SELECT * FROM Invite_Link WHERE token = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return map(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<InviteLink> findByProjectId(int projectId) {
        List<InviteLink> list = new ArrayList<>();
        String sql = "SELECT * FROM Invite_Link WHERE project_id = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, projectId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void updateActive(int id, boolean isActive) {
        String sql = "UPDATE Invite_Link SET is_active = ? WHERE id = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, isActive);
            ps.setInt(2, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Invite_Link WHERE id = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private InviteLink map(ResultSet rs) throws SQLException {
        InviteLink link = new InviteLink();
        link.setId(rs.getInt("id"));
        link.setProjectId(rs.getInt("project_id"));
        link.setToken(rs.getString("token"));
        link.setJoinMode(JoinMode.valueOf(rs.getString("join_mode")));
        link.setActive(rs.getBoolean("is_active"));
        link.setCreatedAt(rs.getTimestamp("created_at"));
        return link;
    }
}