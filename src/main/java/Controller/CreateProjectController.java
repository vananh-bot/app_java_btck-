package Controller;

import DAO.*;
import Service.ProjectService;
import Utils.DialogManager;
import Utils.SceneNavigator;
import Utils.ScreenManager;
import Utils.UserSession;
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

    private ProjectService projectService;

    public CreateProjectController() {
        this.projectService = new ProjectService(new ProjectDAO(), new UserProjectDAO(), new InviteDAO(), new TaskDAO());
    }

    @FXML
    public void createProject(ActionEvent event) {
        String name = enter.getText().trim();
        String description = describe.getText().trim();
        int currentUserId = UserSession.getUserId();

        if (name.isEmpty()) {
            showNotify("Lỗi", "Tên dự án không được để trống!", Alert.AlertType.WARNING);
            return;
        }

        if (projectService.isNameDuplicate(currentUserId, name)) {
            showNotify("Trùng tên", "Dự án '" + name + "' đã tồn tại. Thử tên khác nhé!", Alert.AlertType.ERROR);
            return;
        }

        boolean success = projectService.createProject(name, description, currentUserId);
        if (success) {
            ScreenManager.getInstance().show(Screen.MAIN_PROJECT_VIEW);
        } else {
            showNotify("Thất bại", "Không thể tạo dự án. Hãy thử lại sau!", Alert.AlertType.ERROR);
        }

    }
    @FXML
    private void handleCancel(ActionEvent event) {
        DialogManager.getInstance().close(overlay);
    }


    private void showNotify(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
