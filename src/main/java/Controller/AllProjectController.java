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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import java.util.*;

public class AllProjectController implements Initializable {

    @FXML private FlowPane projectContainer;
    @FXML private TextField searchInput;
    @FXML private Button createProject;

    private ProjectService projectService;
    private List<ProjectCardDTO> allProjectDTOs = new ArrayList<>();
    private Timeline searchDelay;
    private Map<Integer, ProjectCardDTO> oldDataMap = new HashMap<>();
    private Map<Integer, AnchorPane> projectCardMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        projectService = new ProjectService(new ProjectDAO(), new UserProjectDAO(), new InviteDAO(), new TaskDAO());

        int currentUserId = UserSession.getUserId();
        if (currentUserId != -1) {
            projectCardMap.clear();
            oldDataMap.clear();
            loadData(currentUserId);
        }

        // Khởi tạo logic tìm kiếm
        setupSearchLogic();
        Platform.runLater(() -> {
            projectContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.windowProperty().addListener((o, oldWin, newWin) -> {
                        if (newWin != null) {
                            newWin.focusedProperty().addListener((fObs, oldVal, isFocused) -> {
                                if (isFocused) {
                                    loadData(UserSession.getUserId());
                                }
                            });
                        }
                    });
                }
            });
        });
    }

    private void loadData(int userId) {
        new Thread(() -> {
            List<ProjectCardDTO> newList = projectService.getDashboardProjects(userId);

            Map<Integer, ProjectCardDTO> newMap = new HashMap<>();
            for (ProjectCardDTO dto : newList) {
                newMap.put(dto.getProject().getId(), dto);
            }

            Platform.runLater(() -> applyDelta(newMap));
        }).start();
    }
    private void applyDelta(Map<Integer, ProjectCardDTO> newMap) {

        // ADD + UPDATE
        for (Map.Entry<Integer, ProjectCardDTO> entry : newMap.entrySet()) {
            int id = entry.getKey();
            ProjectCardDTO newDto = entry.getValue();

            ProjectCardDTO oldDto = oldDataMap.get(id);

            if (oldDto == null) {
                addProjectCard(newDto);
            } else if (isChanged(oldDto, newDto)) {
                updateProjectCard(newDto);
            }
        }

        // REMOVE
        for (Integer oldId : oldDataMap.keySet()) {
            if (!newMap.containsKey(oldId)) {
                removeProjectCard(oldId);
            }
        }

        oldDataMap = newMap;
        allProjectDTOs = new ArrayList<>(newMap.values());
    }
    private boolean isChanged(ProjectCardDTO oldDto, ProjectCardDTO newDto) {
        return !Objects.equals(oldDto.getProject().getName(), newDto.getProject().getName())
                || !Objects.equals(oldDto.getProject().getDescription(), newDto.getProject().getDescription());
    }
    private AnchorPane createProjectCard(ProjectCardDTO dto) throws Exception {
        URL fxmlLocation = getClass().getResource("/project/projectcard.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        AnchorPane card = loader.load();

        ProjectCardController controller = loader.getController();
        if (controller != null) {
            controller.setProjectData(dto);
            controller.loadTaskStatsAsync(dto.getProject().getId());
            controller.setOnDataUpdated(this::scheduleSort);
        }

        card.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                openProjectDetails(dto.getProject().getId());
            }
        });

        return card;
    }
    private void addProjectCard(ProjectCardDTO dto) {
        try {
            AnchorPane card = createProjectCard(dto);
            projectCardMap.put(dto.getProject().getId(), card);
            projectContainer.getChildren().add(card);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void updateProjectCard(ProjectCardDTO dto) {
        try {
            int id = dto.getProject().getId();
            AnchorPane oldCard = projectCardMap.get(id);
            if (oldCard == null) return;
            AnchorPane newCard = createProjectCard(dto);

            int index = projectContainer.getChildren().indexOf(oldCard);
            projectContainer.getChildren().set(index, newCard);

            projectCardMap.put(id, newCard);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void removeProjectCard(int projectId) {
        AnchorPane card = projectCardMap.get(projectId);
        if (card != null) {
            projectContainer.getChildren().remove(card);
            projectCardMap.remove(projectId);
        }
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
            applyFilter(filtered);
        }));
        searchDelay.setCycleCount(1);

        searchInput.textProperty().addListener((obs, old, newVal) -> {
            searchDelay.stop();
            searchDelay.play();
        });
    }
    private void applyFilter(List<ProjectCardDTO> filteredList) {
        Set<Integer> visibleIds = new HashSet<>();
        for (ProjectCardDTO dto : filteredList) {
            visibleIds.add(dto.getProject().getId());
        }

        for (Map.Entry<Integer, AnchorPane> entry : projectCardMap.entrySet()) {
            boolean visible = visibleIds.contains(entry.getKey());
            entry.getValue().setVisible(visible);
            entry.getValue().setManaged(visible);
        }
    }
    private void sortUI() {
        allProjectDTOs = projectService.sortByScore(new ArrayList<>(allProjectDTOs));

        List<AnchorPane> newOrder = new ArrayList<>();

        for (ProjectCardDTO dto : allProjectDTOs) {
            AnchorPane card = projectCardMap.get(dto.getProject().getId());
            if (card != null) {
                newOrder.add(card);
            }
        }

        projectContainer.getChildren().setAll(newOrder);
    }
    private Timeline sortDelay;

    private void scheduleSort() {
        if (sortDelay == null) {
            sortDelay = new Timeline(
                    new KeyFrame(Duration.millis(200), e -> sortUI())
            );
            sortDelay.setCycleCount(1);
        }
        sortDelay.stop();
        sortDelay.play();
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
    private void switchScene(ActionEvent event, String path) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void createNewProject(ActionEvent event) {
        switchScene(event, "/project/createProject.fxml");
    }
}