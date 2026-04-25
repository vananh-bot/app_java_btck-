package DAO;
import DAO.EmailInviteDAOInterface;
import Model.EmailInvite;
import database.JDBCUtil;
import java.sql.*;
import java.util.*;
import Enum.InviteStatus;

public class EmailInviteDAO implements EmailInviteDAOInterface {

    @Override
    public void create(EmailInvite invite) {
        String sql = "INSERT INTO Email_Invite(project_id, email, token, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, invite.getProjectId());
            ps.setString(2, invite.getEmail());
            ps.setString(3, invite.getToken());
            ps.setString(4, invite.getStatus().name());

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public EmailInvite findByToken(Connection conn, String token) {
        String sql = "SELECT * FROM Email_Invite WHERE token = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return map(rs);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    @Override
    public List<EmailInvite> findByProjectId(int projectId) {
        List<EmailInvite> list = new ArrayList<>();
        String sql = "SELECT * FROM Email_Invite WHERE project_id = ?";

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
    public void updateStatus(Connection conn, int id, InviteStatus status) {
        String sql = "UPDATE Email_Invite SET status = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Email_Invite WHERE id = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private EmailInvite map(ResultSet rs) throws SQLException {
        EmailInvite e = new EmailInvite();
        e.setId(rs.getInt("id"));
        e.setProjectId(rs.getInt("project_id"));
        e.setEmail(rs.getString("email"));
        e.setToken(rs.getString("token"));
        e.setStatus(InviteStatus.valueOf(rs.getString("status")));
        e.setCreatedAt(rs.getTimestamp("created_at"));
        return e;
    }
}