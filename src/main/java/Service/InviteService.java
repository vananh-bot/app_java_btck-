package Service;
import java.sql.Connection;
import java.util.UUID;

import DAO.*;
import Model.*;
import Enum.*;
import database.JDBCUtil;

public class InviteService {

    private InviteLinkDAO inviteLinkDAO;
    private EmailInviteDAO emailInviteDAO;
    private JoinRequestDAO joinRequestDAO;
    private UserProjectDAOInterface userProjectDAO;
    private UserDAO userDAO = new UserDAO();
    private ProjectService projectService = new ProjectService(
            new ProjectDAO()
    );

    public InviteService(InviteLinkDAO linkDAO,
                         EmailInviteDAO emailDAO,
                         JoinRequestDAO requestDAO,
                         UserProjectDAOInterface userProjectDAO) {
        this.inviteLinkDAO = linkDAO;
        this.emailInviteDAO = emailDAO;
        this.joinRequestDAO = requestDAO;
        this.userProjectDAO = userProjectDAO;
    }

    // 🔗 tạo link
    public String createInviteLink(int projectId, JoinMode mode) {
        InviteLink link = new InviteLink();
        link.setProjectId(projectId);
        link.setToken(UUID.randomUUID().toString());
        link.setJoinMode(mode);
        link.setActive(true);

        inviteLinkDAO.create(link);
        return link.getToken();
    }

    // 👤 join bằng link
    public void joinByLink(String token, int userId) {

        InviteLink link = inviteLinkDAO.findByToken(token);

        if (link == null || !link.isActive()) {
            throw new RuntimeException("Link không hợp lệ");
        }

        if (userProjectDAO.exists(userId, link.getProjectId())) {
            return;
        }

        // PUBLIC → vào luôn (transaction)
        if (link.getJoinMode() == JoinMode.PUBLIC) {

            Connection conn = null;

            try {
                conn = JDBCUtil.getConnection();
                conn.setAutoCommit(false);

                userProjectDAO.addMember(conn, userId, link.getProjectId());

                conn.commit();

            } catch (Exception e) {
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

        } else {
            // APPROVAL_REQUIRED → tạo request
            try (Connection conn = JDBCUtil.getConnection()) {

                JoinRequest r = new JoinRequest();
                r.setProjectId(link.getProjectId());
                r.setUserId(userId);
                r.setStatus(RequestStatus.PENDING);

                joinRequestDAO.create(conn, r);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //  mời email
    // Thêm 2 tham số: projectName và inviteeName vào hàm
    public void inviteByEmail(int projectId, String email, String inviteeName) {
        User user = userDAO.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("Email chưa đăng ký tài khoản!");
        }
        if (userProjectDAO.exists(user.getId(), projectId)) {
            throw new RuntimeException("Người này đã ở trong dự án!");
        }

        // Gọi hàm getProjectName từ ProjectService như bạn yêu cầu
        String projectName = projectService.getProjectName(projectId);

        // 2. Tạo mã Token (cắt ngắn 8 ký tự cho đẹp và dễ copy)
        String token = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 3. Lưu bản ghi lời mời vào Database
        EmailInvite invite = new EmailInvite();
        invite.setProjectId(projectId);
        invite.setEmail(email);
        invite.setToken(token);
        invite.setStatus(InviteStatus.PENDING);

        emailInviteDAO.create(invite);

        // 4. Gọi EmailService để gửi thư
        EmailService emailService = new EmailService();
        emailService.sendInvite(email, inviteeName, projectName, token);
    }

    //  accept email (transaction chuẩn)
    public int acceptEmailInvite(String token, int userId, String currentUserEmail) throws Exception {
        Connection conn = null;
        try {
            conn = JDBCUtil.getConnection();
            conn.setAutoCommit(false);

            EmailInvite invite = emailInviteDAO.findByToken(conn, token);

            // 1. Kiểm tra mã tồn tại / sai mã
            if (invite == null || invite.getStatus() != InviteStatus.PENDING) {
                // Ném thẳng câu chữ ra ngoài
                throw new Exception("Mã mời không tồn tại hoặc đã được sử dụng!");
            }

            // 2. Kiểm tra hết hạn 30 phút
            long currentTime = System.currentTimeMillis();
            long inviteTime = invite.getCreatedAt().getTime();
            if (currentTime - inviteTime > (30 * 60 * 1000)) {
                emailInviteDAO.updateStatus(conn, invite.getId(), InviteStatus.EXPIRED);
                conn.commit(); // Vẫn phải commit để lưu trạng thái Hết hạn vào DB

                throw new Exception("Mã mời đã hết hạn (quá 30 phút). Vui lòng xin mã mới!");
            }
            // Dán 2 dòng này vào để in ra xem rốt cuộc 2 cái email chứa chữ gì
            System.out.println("1. Email trong Database: [" + invite.getEmail() + "]");
            System.out.println("2. Email đang Login: [" + currentUserEmail + "]");

            // 3. Kiểm tra đúng người (Đúng Email)
            if (!invite.getEmail().equalsIgnoreCase(currentUserEmail)) {
                throw new Exception("Mã mời này không dành cho tài khoản email của bạn!");
            }

            // --- Vượt qua 3 ải kiểm tra, tiến hành thêm user ---
            userProjectDAO.addMember(conn, userId, invite.getProjectId());
            emailInviteDAO.updateStatus(conn, invite.getId(), InviteStatus.ACCEPTED);

            conn.commit();
            return invite.getProjectId();

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            }
            // Quan trọng: Ném lỗi tiếp ra ngoài để Controller bắt được
            throw e;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (Exception ex) { ex.printStackTrace(); }
            }
        }
    }

    // reject
    public void rejectEmailInvite(String token) {

        try (Connection conn = JDBCUtil.getConnection()) {

            EmailInvite invite = emailInviteDAO.findByToken(conn, token);

            if (invite == null) {
                throw new RuntimeException("Invite không tồn tại");
            }

            emailInviteDAO.updateStatus(conn, invite.getId(), InviteStatus.REJECTED);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}