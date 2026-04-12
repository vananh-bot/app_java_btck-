package Controller;

import DTO.ProjectCardDTO;
import Service.ProjectService;
import Utils.UserSession;
import Utils.SceneNavigator;
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

        // Khởi tạo logic tìm kiếm
        setupSearchLogic();
    }

    private void loadData(int userId) {
        new Thread(() -> {
            allProjectDTOs = projectService.getDashboardProjects(userId);
            Platform.runLater(() -> renderProjects(allProjectDTOs));
        }).start();
    }

    private void setupSearchLogic() {
        // Kiểm tra an toàn để tránh NullPointerException
        if (searchInput == null) return;

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
        if (projectContainer == null) {
            System.err.println("LỖI: projectContainer is NULL. Hãy kiểm tra fx:id trong FXML chính!");
            return;
        }

        projectContainer.getChildren().clear();
        System.out.println("Đang bắt đầu render " + list.size() + " dự án...");

        for (ProjectCardDTO dto : list) {
            try {
                // Thử dùng cách lấy Resource an toàn hơn
                URL fxmlLocation = getClass().getResource("/project/projectcard.fxml");
                if (fxmlLocation == null) {
                    System.err.println("LỖI: Không tìm thấy file /view/project_card.fxml");
                    continue;
                }

                FXMLLoader loader = new FXMLLoader(fxmlLocation);
                AnchorPane card = loader.load();

                ProjectCardController controller = loader.getController();
                if (controller != null) {
                    controller.setProjectData(dto);
                }

                card.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2) {
                        openProjectDetails(dto.getProject().getId());
                    }
                });

                projectContainer.getChildren().add(card);
            } catch (Exception e) {
                System.err.println("Lỗi nghiêm trọng khi nạp Card: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void openProjectDetails(int projectId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/project/mainProjectView.fxml"));
            Parent root = loader.load();
            MainProjectController controller = loader.getController();
            controller.init(projectId);

            javafx.stage.Stage stage = (javafx.stage.Stage) projectContainer.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("FlowTask - Chi tiết dự án");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String normalize(String text) {
        if (text == null) return "";
        String n = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return n.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase().trim();
    }

    // --- XỬ LÝ CHUYỂN MÀN HÌNH ---

    public void handleDashboard(ActionEvent event) {
        SceneNavigator.switchScene(event, SceneNavigator.DASHBOARD, "Tổng quan");
    }

    public void handleMyProjects(ActionEvent event) {
        SceneNavigator.switchScene(event, SceneNavigator.ALL_PROJECTS, "Dự án của tôi");
    }

    public void handleNotification(ActionEvent event) {
        SceneNavigator.switchScene(event, SceneNavigator.NOTIFICATION, "Thông báo");
    }

    public void handleLogout(ActionEvent event) {
        UserSession.logout();
        SceneNavigator.switchScene(event, SceneNavigator.LOGIN, "Đăng nhập");
    }
}