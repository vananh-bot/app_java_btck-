package Controller;

import Cache.ProjectCache;
import DAO.*;
import DTO.ProjectDashboardDTO;
import Service.ProjectService;
import Utils.DialogManager;
import Utils.ScreenManager;
import Utils.UserSession;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import javafx.event.ActionEvent;
import Enum.Screen;

public class CreateProjectController {
    @FXML private TextArea enter;
    @FXML private TextArea describe;
    @FXML private StackPane overlay;
    @FXML
    private Button create;
    @FXML
    private ProgressIndicator loading;
    @FXML
    private Label alert;

    private ProjectService projectService;

    private ProjectCache projectCache = ProjectCache.getInstance();

    public CreateProjectController() {
        this.projectService = new ProjectService(new ProjectDAO(), new UserProjectDAO());
    }

    @FXML
    private void initialize(){
        alert.setText("");
        loading.setVisible(false);
    }

    @FXML
    public void createProject(ActionEvent event) {
        String name = enter.getText().trim();
        String description = describe.getText().trim();
        int currentUserId = UserSession.getUserId();
        String currentUserName = UserSession.getCurrentUser().getName();

        if (name.isEmpty()) {
            alert.setText("Tên dự án không được để trống!");
            return;
        }

        loading.setVisible(true);
        create.setText("");
        create.setDisable(true);
        loading.setProgress(-1);

        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                return projectService.createProject(name, description, currentUserId);
            }
        };

        task.setOnSucceeded(e -> {
            loading.setVisible(false);
            create.setText("Tạo dự án");
            create.setDisable(false);

            int projectId = task.getValue();

            if(projectId > 0){
                ProjectDashboardDTO project = new ProjectDashboardDTO(projectId, name, 0, 0, 0, currentUserId, currentUserName, projectService.convertToPreviewDescription(description));
                projectCache.put(project);

                ScreenManager.getInstance().show(Screen.MAIN_PROJECT_VIEW, projectId);
            } else {
                alert.setText("Không thể tạo dự án!");
            }
        });
        task.setOnFailed(e -> showError());

        new Thread(task).start();
    }

    private void showError(){
        loading.setVisible(false);
        create.setText("Tạo dự án");
        create.setDisable(false);
        alert.setText("Dự án bị trùng tên! Vui lòng nhập tên khác");
    }
    @FXML
    private void handleCancel(ActionEvent event) {
        DialogManager.getInstance().close(overlay);
    }

}
