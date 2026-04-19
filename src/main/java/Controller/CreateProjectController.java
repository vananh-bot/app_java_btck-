package Controller;

import DAO.InviteDAO;
import DAO.UserProjectDAO;
import Service.ProjectService;
import Utils.UserSession;
import javafx.fxml.FXML;
import DAO.UserDAO;
import DAO.ProjectDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.IOException;
public class CreateProjectController {
    @FXML private TextArea enter;
    @FXML private TextArea describe;

    private ProjectService projectService;

    public CreateProjectController() {
        this.projectService = new ProjectService(new ProjectDAO(), new UserProjectDAO(), new InviteDAO());
    }

    @FXML
    public void createProject() {
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
            showNotify("Thành công", "Tạo dự án thành công", Alert.AlertType.INFORMATION);
            closeWindow();
        } else {
            showNotify("Thất bại", "Không thể tạo dự án. Hãy thử lại sau!", Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) enter.getScene().getWindow();
        stage.close();
    }

    private void showNotify(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
