package Service;

import DAO.NotificationDAO;
import DAO.ProjectDAO;
import DAO.TaskDAO;
import DAO.UserProjectDAO;
import DTO.TaskDashboardDTO;
import Enum.NotificationType;
import DTO.NotificationDTO;
import Model.Notification;
import Utils.TimeUtil;

import java.util.List;

public class NotificationService {

    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final TaskDAO taskDAO = new TaskDAO();
    private final MailService mailService = new MailService();
    private final ProjectDAO projectDAO = new ProjectDAO();
    private final UserProjectDAO userProjectDAO = new UserProjectDAO();
    // ================= GET ALL (DTO - UI READY) =================
    public List<NotificationDTO> getNotificationDTOByUserId(int userId) {
        List<NotificationDTO> list =
                notificationDAO.getNotificationDTOByUserId(userId);

        // ===== PRE-COMPUTE UI FIELDS (QUAN TRỌNG) =====
        for (NotificationDTO dto : list) {
            enrichDTO(dto);
        }

        return list;
    }

    // ================= GET UNREAD (DTO) =================
    public List<NotificationDTO> getUnreadDTOByUserId(int userId) {
        List<NotificationDTO> list =
                notificationDAO.getUnreadNotificationDTOByUserId(userId);

        for (NotificationDTO dto : list) {
            enrichDTO(dto);
        }

        return list;
    }

    // ================= MARK AS READ =================
    public void markAsRead(int id) {
        notificationDAO.markAsRead(id);
    }

    public void markAllAsRead(int userId) {
        notificationDAO.markAllAsRead(userId);
    }

    // ================= COUNT (OPTIMIZED - NO FULL LOAD) =================
    public int countAll(int userId) {
        return notificationDAO.countAll(userId);
    }

    public int countUnread(int userId) {
        return notificationDAO.countUnread(userId);
    }

    // ================= FILTER BY TYPE =================
    public List<NotificationDTO> getByType(int userId, NotificationType type) {
        List<NotificationDTO> list =
                notificationDAO.getNotificationDTOByUserIdAndType(userId, type);

        for (NotificationDTO dto : list) {
            enrichDTO(dto);
        }

        return list;
    }

    // ================= CORE: ENRICH DTO FOR UI =================
    private void enrichDTO(NotificationDTO dto) {

        // ===== TIME CACHE =====
        dto.setTimeText(TimeUtil.toRelative(dto.getCreatedAt()));

        // ===== TYPE MAPPING (ICON + MESSAGE + THEME) =====
        switch (dto.getType()) {

            case COMMENT -> {
                dto.setIconPath("/images/comment.png");
                dto.setThemeClass("purple");

                dto.setMessage(
                        dto.getCreatorName()
                                + " đã bình luận trong task \""
                                + dto.getTaskTitle() + "\""
                );
            }

            case DEADLINE -> {
                dto.setIconPath("/images/deadline2.png");
                dto.setThemeClass("yellow");

                dto.setMessage(
                        "Task \"" + dto.getTaskTitle() + "\" sắp đến hạn"
                );
            }
            case JOIN_PROJECT -> {
                dto.setIconPath("/images/new_member.png");
                dto.setThemeClass("green");
                dto.setMessage(dto.getCreatorName() + " đã tham gia vào dự án");
            }
        }
    }
    public void scanAndSendOverdueEmailsOnly() {
        List<TaskDashboardDTO> overdueTasks = taskDAO.getTasksForOverdueEmail();
        for (TaskDashboardDTO task : overdueTasks) {
            mailService.sendTaskOverdue(
                    task.getUserEmail(),
                    task.getProjectName(),
                    task.getTitle(),
                    "ngay bây giờ"
            );
            taskDAO.markAsOverdueNotified(task.getId());
        }
    }
    public void notifyNewMemberJoined(int projectId, int newMemberId, String userName) {
        new Thread(() -> {
            try {
                String projectName = projectDAO.getProjectNameById(projectId);

                // 2. Lấy danh sách email của tất cả thành viên CŨ trong dự án
                List<String> emails = userProjectDAO.getMemberEmailsByProjectId(projectId, newMemberId);

                // 3. Bắn mail cho từng người
                if (emails != null && !emails.isEmpty()) {
                    for (String email : emails) {
                        mailService.sendJoinSuccessEmail(email, projectName, userName);
                    }
                    System.out.println("==> FlowTask: Đã gửi thông báo thành viên mới cho " + emails.size() + " người.");
                }
            } catch (Exception e) {
                System.err.println("==> FlowTask LỖI: Không thể gửi thông báo gia nhập dự án.");
                e.printStackTrace();
            }
        }).start();
    }
}