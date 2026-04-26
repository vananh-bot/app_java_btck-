package Controller;

import DAO.*;
import Service.ProjectService;
import Utils.DialogManager;
import Utils.SceneNavigator;
import Utils.ScreenManager;
import Utils.UserSession;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.IOException;
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

    public CreateProjectController() {
        this.projectService = new ProjectService(new ProjectDAO(), new UserProjectDAO(), new InviteDAO(), new TaskDAO());
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
                if (projectService.isNameDuplicate(currentUserId, name)) {
                    return -2;
                }
                return projectService.createProject(name, description, currentUserId);
            }
        };

        task.setOnSucceeded(e -> {
            loading.setVisible(false);
            create.setText("Tạo dự án");
            create.setDisable(false);

            int projectId = task.getValue();

            if(projectId == -2){
                alert.setText("Trùng tên dự án. Thử tên khác nhé!");
            } else if(projectId > 0){
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
        alert.setText("Lỗi hệ thống!");
    }
    @FXML
    private void handleCancel(ActionEvent event) {
        DialogManager.getInstance().close(overlay);
    }

}
