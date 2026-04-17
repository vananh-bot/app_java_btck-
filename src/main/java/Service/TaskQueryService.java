package Service;

import DAO.TaskDAO;
import Enum.TaskStatus;
import Model.Task;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class TaskQueryService {
    private TaskDAO taskDAO = new TaskDAO();
    private List<Task> cachedTasks = new ArrayList<>();
    private int projectId;
    private Timeline realtimeTimeline;

    public void init(int projectId) {
        this.projectId = projectId;
        this.cachedTasks.clear();
    }

    public List<Task> getCachedTasks() {
        if (cachedTasks.isEmpty()) {
            cachedTasks = taskDAO.getTasksByProjectId(projectId);
        }
        return cachedTasks;
    }

    public void forceRefreshCache() {
        cachedTasks.clear();
    }

    public void updateTaskStatusAsync(int taskId, TaskStatus status) {
        new Thread(() -> {
            taskDAO.updateStatus(taskId, status);
        }).start();
    }

    public void startRealtimeUpdates(Runnable onDataChanged) {
        if (realtimeTimeline != null) realtimeTimeline.stop();

        realtimeTimeline = new Timeline(
                new KeyFrame(Duration.seconds(30), e -> {
                    new Thread(() -> {
                        List<Task> freshTasks = taskDAO.getTasksByProjectId(projectId);
                        if (freshTasks.size() != cachedTasks.size()) { // Logic so sánh của bạn
                            Platform.runLater(() -> {
                                cachedTasks = freshTasks;
                                onDataChanged.run();
                            });
                        }
                    }).start();
                })
        );
        realtimeTimeline.setCycleCount(Timeline.INDEFINITE);
        realtimeTimeline.play();
    }
}