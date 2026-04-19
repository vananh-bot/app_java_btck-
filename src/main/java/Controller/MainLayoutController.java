package Controller;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.layout.StackPane;

public class MainLayoutController {

    @FXML
    private StackPane contentArea;

    @FXML
    private SidebarController sidebarIncludeController;

    @FXML
    public void initialize() {
        // 🔥 gắn sidebar với main
        sidebarIncludeController.setMain(this);

        // load màn mặc định
        loadPage("/project/AllMyProjectView.fxml");
    }

    // 🔥 load màn bình thường
    public void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            contentArea.getChildren().setAll(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 load màn + truyền data
    public void openProjectDetail(int projectId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/project/MainProject.fxml"));
            Parent view = loader.load();

            MainProjectController controller = loader.getController();

            if (controller == null) {
                throw new RuntimeException("Controller NULL - check fx:controller");
            }

            controller.init(projectId); // 🔥 chỗ quan trọng

            contentArea.getChildren().setAll(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}