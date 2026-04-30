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

    // 👉 callback mở project
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

    // 👉 nhận event từ NotificationController
    public void setOpenProjectHandler(Consumer<Integer> handler) {
        this.openProjectHandler = handler;
    }

    @Override
    protected void updateItem(NotificationDTO item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
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

                controller.setServices(
                        notificationService,
                        projectService,
                        taskService
                );

                // 👉 forward click project từ ItemController
                controller.setOpenProjectHandler(projectId -> {
                    if (openProjectHandler != null) {
                        openProjectHandler.accept(projectId);
                    }
                });
            }

            controller.setData(item);

            setText(null);
            setGraphic(view);

        } catch (Exception e) {
            e.printStackTrace();
            setGraphic(null);
        }

        controller.setOpenTaskHandler(taskId -> {
            if (openTaskHandler != null) {
                openTaskHandler.accept(taskId);
            }
        });
    }

    private Consumer<Integer> openTaskHandler;

    public void setOpenTaskHandler(Consumer<Integer> handler) {
        this.openTaskHandler = handler;
    }
}