package Service;

import DAO.NotificationDAO;
import Enum.NotificationType;
import Model.NotificationDTO;
import Utils.TimeUtil;

import java.util.List;

public class NotificationService {

    private final NotificationDAO notificationDAO = new NotificationDAO();

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


        }
    }
}