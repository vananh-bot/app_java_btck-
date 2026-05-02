package Controller;

import Model.NotificationDTO;
import Service.NotificationService;
import Service.ProjectService;
import Service.TaskService;
import Service.UserService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;

import java.util.function.Consumer;

public class NotificationCell extends ListCell<NotificationDTO> {

    private final NotificationService notificationService;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final UserService userService;

    private Node view;
    private ItemController controller;

    private Consumer<Integer> openProjectHandler;

    public NotificationCell(NotificationService ns,
                            ProjectService ps,
                            TaskService ts,
                            UserService us) {

        this.notificationService = ns;
        this.projectService = ps;
        this.taskService = ts;
        this.userService = us;
    }

    public void setOpenProjectHandler(Consumer<Integer> handler) {
        this.openProjectHandler = handler;
    }

    @Override
    protected void updateItem(NotificationDTO item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
            return;
        }

        try {
            if (view == null) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/notification/item.fxml")
                );

                view = loader.load();
                controller = loader.getController();

                controller.setServices(notificationService, projectService, taskService);
            }

            // 🔥 LUÔN SET LẠI HANDLER (rất quan trọng)
            controller.setOpenProjectHandler(projectId -> {
                if (openProjectHandler != null) {
                    openProjectHandler.accept(projectId);
                }
            });

            controller.setOpenTaskHandler(taskId -> {
                if (openTaskHandler != null) {
                    openTaskHandler.accept(taskId);
                }
            });

            // 🔥 LUÔN SET DATA
            controller.setData(item);

            setGraphic(view);

        } catch (Exception e) {
            e.printStackTrace();
            setGraphic(null);
        }
    }

    private Consumer<Integer> openTaskHandler;

    public void setOpenTaskHandler(Consumer<Integer> handler) {
        this.openTaskHandler = handler;
    }
}