package DAO;

import Model.JoinRequest;

import java.sql.Connection;
import java.util.List;
import Enum.RequestStatus;

public interface JoinRequestDAOInterface {
    List<JoinRequest> findByProject(int projectId);
    List<JoinRequest> findByUser(int userId);
    void updateStatus(Connection conn, int id, RequestStatus status);
    JoinRequest findById(Connection conn, int id);
    void create(Connection conn, JoinRequest request);
}


