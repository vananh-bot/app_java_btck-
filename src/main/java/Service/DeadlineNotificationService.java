package Service;

import DAO.NotificationDAO;
import DAO.ProjectDAO;
import DAO.TaskDAO;
import Enum.NotificationType;
import Model.Notification;
import Model.Task;
import Utils.AppEventBus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class DeadlineNotificationService {

    private final TaskDAO taskDAO = new TaskDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final ProjectDAO projectDAO = new ProjectDAO();

    public void checkUpcomingDeadlines() {

        List<Task> allTasks = taskDAO.getAllTasks();

        System.out.println("CHECK DEADLINE RUNNING | total = " + allTasks.size());

        for (Task task : allTasks) {

            if (task.getDeadline() == null) continue;

            long minutesLeft = Duration
                    .between(LocalDateTime.now(), task.getDeadline())
                    .toMinutes();

            System.out.println(task.getTitle() + " -> " + minutesLeft);

            if (minutesLeft > 0 && minutesLeft <= 1440) {

                Integer projectId = task.getProjectId();
                if (projectId == null) continue;

                List<Integer> memberIds = projectDAO.getMemberIds(projectId);
                boolean hasNew = false;

                for (Integer memberId : memberIds) {

                    if (notificationDAO.existsDeadlineNotification(memberId, task.getId()))
                        continue;

                    Notification n = new Notification();
                    n.setUserId(memberId);
                    n.setProjectId(projectId);
                    n.setTaskId(task.getId());
                    n.setTitle("Deadline sắp đến");
                    n.setMessage("Task \"" + task.getTitle() + "\" sẽ đến hạn trong 1 ngày");
                    n.setType(NotificationType.DEADLINE);
                    n.setRead(false);
                    hasNew = true;
                    notificationDAO.insert(n);
                }

                if (hasNew) {
                    AppEventBus.emitNotificationChange();
                }
            }
        }

        AppEventBus.emitNotificationChange();
    }
}