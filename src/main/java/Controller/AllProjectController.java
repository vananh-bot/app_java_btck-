package Controller;

import DTO.ProjectCardDTO;
import Service.ProjectService;
import Utils.UserSession;
import Utils.SceneNavigator; // 1. Import Navigator chung
import DAO.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.util.Duration;

import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AllProjectController implements Initializable {

    @FXML private FlowPane projectContainer;
    @FXML private TextField searchInput;

    private ProjectService projectService;
    private List<ProjectCardDTO> allProjectDTOs = new ArrayList<>();
    private Timeline searchDelay;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        projectService = new ProjectService(new ProjectDAO(), new UserProjectDAO(), new InviteDAO(), new TaskDAO());

        int currentUserId = UserSession.getUserId();

        if (currentUserId != -1) {
            loadData(currentUserId);
        }

        setupSearchLogic();
    }

    private void loadData(int userId) {
        new Thread(() -> {
            allProjectDTOs = projectService.getDashboardProjects(userId);
            Platform.runLater(() -> renderProjects(allProjectDTOs));
        }).start();
    }

    private void setupSearchLogic() {
        searchDelay = new Timeline(new KeyFrame(Duration.millis(300), e -> {
            String keyword = normalize(searchInput.getText());
            List<ProjectCardDTO> filtered = new ArrayList<>();
            for (ProjectCardDTO dto : allProjectDTOs) {
                if (normalize(dto.getProject().getName()).contains(keyword)) {
                    filtered.add(dto);
                }
            }
            renderProjects(filtered);
        }));
        searchDelay.setCycleCount(1);
        searchInput.textProperty().addListener((obs, old, newVal) -> {
            searchDelay.stop();
            searchDelay.play();
        });
    }

    private void renderProjects(List<ProjectCardDTO> list) {
        projectContainer.getChildren().clear();
        for (ProjectCardDTO dto : list) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/project_card.fxml"));
                AnchorPane card = loader.load();

                ProjectCardController controller = loader.getController();
                controller.setProjectData(dto);

                // 2. Sự kiện click đúp để vào dự án (Vẫn cần init dữ liệu nên giữ logic nạp controller)
                card.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2) {
                        openProjectDetails(dto.getProject().getId());
                    }
                });

                projectContainer.getChildren().add(card);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    // Hàm này giữ lại vì cần truyền tham số projectId vào Controller đích
    private void openProjectDetails(int projectId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main_project.fxml"));
            Parent root = loader.load();
            MainProjectController controller = loader.getController();
            controller.init(projectId);

            // Sử dụng Stage từ container hiện tại
            javafx.stage.Stage stage = (javafx.stage.Stage) projectContainer.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("FlowTask - Chi tiết dự án");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private String normalize(String text) {
        if (text == null) return "";
        String n = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return n.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase().trim();
    }

    // --- 3. SỬ DỤNG CHUYỂN MÀN CHUNG CHO SIDEBAR ---

    public void handleDashboard(ActionEvent event) {
        Utils.SceneNavigator.switchScene(event, Utils.SceneNavigator.DASHBOARD, "Tổng quan");
    }

    public void handleMyProjects(ActionEvent event) {
        Utils.SceneNavigator.switchScene(event, Utils.SceneNavigator.ALL_PROJECTS, "Dự án của tôi");
    }

    public void handleNotification(ActionEvent event) {
        Utils.SceneNavigator.switchScene(event, Utils.SceneNavigator.NOTIFICATION, "Thông báo");
    }

    public void handleLogout(ActionEvent event) {
        Utils.UserSession.logout();
        Utils.SceneNavigator.switchScene(event, Utils.SceneNavigator.LOGIN, "Đăng nhập");
    }
}