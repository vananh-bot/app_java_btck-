package Service;
import java.sql.Connection;

import DAO.JoinRequestDAO;
import DAO.UserProjectDAOInterface;
import Model.JoinRequest;
import Enum.RequestStatus;
import database.JDBCUtil;

public class JoinRequestService {

    private JoinRequestDAO joinRequestDAO;
    private UserProjectDAOInterface userProjectDAO;

    public JoinRequestService(JoinRequestDAO dao,
                              UserProjectDAOInterface userProjectDAO) {
        this.joinRequestDAO = dao;
        this.userProjectDAO = userProjectDAO;
    }

    //  User gửi yêu cầu
    public void sendRequest(int projectId, int userId) {

        // đã ở trong project rồi thì không gửi nữa
        if (userProjectDAO.exists(userId, projectId)) {
            return;
        }

        try (Connection conn = JDBCUtil.getConnection()) {

            JoinRequest request = new JoinRequest();
            request.setProjectId(projectId);
            request.setUserId(userId);
            request.setStatus(RequestStatus.PENDING);

            // dùng version có Connection
            joinRequestDAO.create(conn, request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  Admin duyệt (CÓ TRANSACTION)
    public void approveRequest(int requestId) {
        Connection conn = null;

        try {
            conn = JDBCUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. lấy request
            JoinRequest request = joinRequestDAO.findById(conn, requestId);

            if (request == null) {
                throw new RuntimeException("Request không tồn tại");
            }

            // 2. update status
            joinRequestDAO.updateStatus(conn, requestId, RequestStatus.APPROVED);

            // 3. thêm user vào project
            userProjectDAO.addMember(conn, request.getUserId(), request.getProjectId());

            // 4. commit
            conn.commit();

        } catch (Exception e) {

            // rollback nếu lỗi
            try {
                if (conn != null) conn.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            e.printStackTrace();

        } finally {
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //  Admin từ chối
    public void rejectRequest(int requestId) {
        Connection conn = null;

        try {
            conn = JDBCUtil.getConnection();

            // không cần transaction vì chỉ 1 câu SQL
            joinRequestDAO.updateStatus(conn, requestId, RequestStatus.REJECTED);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}