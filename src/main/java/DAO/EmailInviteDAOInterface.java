package DAO;


import Model.EmailInvite;

import java.sql.Connection;
import java.util.List;
import Enum.InviteStatus;

public interface EmailInviteDAOInterface {
    void create(EmailInvite invite);
    List<EmailInvite> findByProjectId(int projectId);
    void delete(int id);
    void updateStatus(Connection conn, int id, InviteStatus status);
    EmailInvite findByToken(Connection conn, String token);
}