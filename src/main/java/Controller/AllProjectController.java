package Controller;

import DTO.ProjectCardDTO;
import Service.ProjectService;
import Utils.UserSession; // Đảm bảo bạn đã tạo class UserSession như mình hướng dẫn
import DAO.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
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
        // 1. Khởi tạo Service với các DAO
        projectService = new ProjectService(new ProjectDAO(), new UserProjectDAO(), new InviteDAO(), new TaskDAO());

        // 2. Lấy ID từ Session thật (Không fix cứng nữa)
        int currentUserId = UserSession.getUserId();

        // 3. Tải dữ liệu từ Backend
        if (currentUserId != -1) {
            loadData(currentUserId);
        }

        // 4. Setup tìm kiếm mượt mà (giống MainProject)
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

                // Gửi dữ liệu vào CardController
                ProjectCardController controller = loader.getController();
                controller.setProjectData(dto);

                // Sự kiện click đúp để vào dự án
                card.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2) {
                        openProject(dto.getProject().getId());
                    }
                });

                projectContainer.getChildren().add(card);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void openProject(int projectId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main_project.fxml"));
            Parent root = loader.load();
            MainProjectController controller = loader.getController();
            controller.init(projectId); // Gọi hàm init có sẵn của bạn

            Stage stage = (Stage) projectContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private String normalize(String text) {
        if (text == null) return "";
        String n = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return n.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase().trim();
    }

    // --- Các hàm chuyển màn hình Sidebar ---
    public void handleDashboard(ActionEvent event) { switchScene(event, "/view/dashboard.fxml"); }
    public void handleMyProjects(ActionEvent event) { switchScene(event, "/view/all_project.fxml"); }
    public void handleLogout(ActionEvent event) { UserSession.logout(); switchScene(event, "/view/login.fxml"); }

    private void switchScene(ActionEvent event, String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) { e.printStackTrace(); }
    }
}