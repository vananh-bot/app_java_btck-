package Controller;

import DTO.ProjectCardDTO;
import Service.ProjectService;
import Utils.DialogManager;
import Utils.ScreenManager;
import Utils.UserSession;
import DAO.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.text.Normalizer;
import java.util.*;
import Enum.Screen;

public class AllProjectController implements Initializable {

    @FXML private FlowPane projectContainer;
    @FXML private TextField searchInput;
    @FXML private Button createProject;
    @FXML
    private ProgressIndicator loading;

    @FXML
    private VBox emptyProject;

    private ProjectService projectService;
    private List<ProjectCardDTO> allProjectDTOs = new ArrayList<>();

    // Timer cho Search và Sort
    private Timeline searchDelay;
    private Timeline sortDelay;

    // Lưu trữ trạng thái UI
    private Map<Integer, ProjectCardDTO> oldDataMap = new HashMap<>();
    private Map<Integer, AnchorPane> projectCardMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        projectService = new ProjectService(new ProjectDAO(), new UserProjectDAO(), new InviteDAO(), new TaskDAO());

        setupSearchLogic();

        Platform.runLater(() -> {
            int currentUserId = UserSession.getUserId();
            if (currentUserId != -1) {
                projectCardMap.clear();
                oldDataMap.clear();
                loadData(currentUserId);
            } else {
                System.err.println("Cảnh báo: UserId đang là -1!");
            }

            if (projectContainer.getScene() != null) {
                projectContainer.getScene().windowProperty().addListener((obsWin, oldWin, newWin) -> {
                    if (newWin != null) {
                        newWin.focusedProperty().addListener((obsFocus, oldFocus, isFocused) -> {
                            if (isFocused) {
                                int userId = UserSession.getUserId();
                                if (userId != -1) {
                                    loadData(userId);
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void loadData(int userId) {
        showLoading(true);
        new Thread(() -> {
            try {
                long startTime = System.currentTimeMillis();
                List<ProjectCardDTO> newList = projectService.getAllMyProjects(userId);
                long endTime = System.currentTimeMillis();

                System.out.println("Đã tải " + newList.size() + " dự án. Thời gian DB: " + (endTime - startTime) + "ms");

                Map<Integer, ProjectCardDTO> newMap = new HashMap<>();
                for (ProjectCardDTO dto : newList) {
                    newMap.put(dto.getProject().getId(), dto);
                }

                Platform.runLater(() -> {
                    applyDeltaUISafely(newMap);
                    updateEmptyState();
                    showLoading(false);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showLoading(boolean b){
        loading.setVisible(b);
        loading.setManaged(b);
        loading.setProgress(-1);
    }

    private void applyDeltaUISafely(Map<Integer, ProjectCardDTO> newMap) {
        try {
            // 1. Update / Add
            for (Map.Entry<Integer, ProjectCardDTO> entry : newMap.entrySet()) {
                int id = entry.getKey();
                ProjectCardDTO newDto = entry.getValue();
                ProjectCardDTO oldDto = oldDataMap.get(id);

                if (oldDto == null) {
                    AnchorPane newCard = createProjectCard(newDto);
                    projectCardMap.put(id, newCard);
                    projectContainer.getChildren().add(newCard);
                } else if (isChanged(oldDto, newDto)) {
                    AnchorPane newCard = createProjectCard(newDto);
                    AnchorPane oldCard = projectCardMap.get(id);

                    int index = projectContainer.getChildren().indexOf(oldCard);
                    if (index != -1) {
                        projectContainer.getChildren().set(index, newCard);
                    }
                    projectCardMap.put(id, newCard);
                }
            }

            // 2. Remove
            List<Integer> toRemove = new ArrayList<>();
            for (Integer oldId : oldDataMap.keySet()) {
                if (!newMap.containsKey(oldId)) {
                    toRemove.add(oldId);
                }
            }
            for (Integer oldId : toRemove) {
                removeProjectCard(oldId);
            }

            oldDataMap = newMap;
            allProjectDTOs = new ArrayList<>(newMap.values());

            // 3. Giữ nguyên trạng thái Search nếu đang gõ dở
            String keyword = normalize(searchInput.getText());
            if (!keyword.isEmpty()) {
                triggerSearch(keyword);
            }

            // 4. CHẠY LẠI LOGIC SẮP XẾP CỦA BẠN (Sắp xếp sau khi có data mới)
            scheduleSort();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* =========================================
     * LOGIC TÌM KIẾM
     * ========================================= */
    private void setupSearchLogic() {
        if (searchInput == null) return;
        searchDelay = new Timeline(new KeyFrame(Duration.millis(300), e -> {
            triggerSearch(normalize(searchInput.getText()));
        }));
        searchDelay.setCycleCount(1);
        searchInput.textProperty().addListener((obs, old, newVal) -> {
            searchDelay.stop();
            searchDelay.play();
        });
    }

    private void triggerSearch(String keyword) {
        Set<Integer> visibleIds = new HashSet<>();
        for (ProjectCardDTO dto : allProjectDTOs) {
            if (normalize(dto.getProject().getName()).contains(keyword)) {
                visibleIds.add(dto.getProject().getId());
            }
        }
        for (Map.Entry<Integer, AnchorPane> entry : projectCardMap.entrySet()) {
            boolean visible = visibleIds.contains(entry.getKey());
            entry.getValue().setVisible(visible);
            entry.getValue().setManaged(visible);
        }
        updateEmptyState();
    }

    private String normalize(String text) {
        if (text == null) return "";
        String n = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return n.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase().trim();
    }

    /* =========================================
     * LOGIC SẮP XẾP ĐÃ ĐƯỢC KHÔI PHỤC
     * ========================================= */
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

    /* =========================================
     * TIỆN ÍCH KHÁC
     * ========================================= */
    private boolean isChanged(ProjectCardDTO oldDto, ProjectCardDTO newDto) {
        return !Objects.equals(oldDto.getProject().getName(), newDto.getProject().getName())
                || !Objects.equals(oldDto.getProject().getDescription(), newDto.getProject().getDescription())
                || oldDto.getTodoCount() != newDto.getTodoCount()
                || oldDto.getInProgressCount() != newDto.getInProgressCount()
                || oldDto.getDoneCount() != newDto.getDoneCount();
    }

    private AnchorPane createProjectCard(ProjectCardDTO dto) throws Exception {
        URL fxmlLocation = getClass().getResource("/project/projectcard.fxml");
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

        return card;
    }

    private void removeProjectCard(int projectId) {
        AnchorPane card = projectCardMap.get(projectId);
        if (card != null) {
            projectContainer.getChildren().remove(card);
            projectCardMap.remove(projectId);
        }
        updateEmptyState();
    }
    private void updateEmptyState(){
        boolean hasProject = projectContainer.getChildren().stream().anyMatch(Node::isVisible);

        emptyProject.setVisible(!hasProject);
        emptyProject.setManaged(!hasProject);
    }

    private void openProjectDetails(int projectId) {
        ScreenManager.getInstance().show(Screen.MAIN_PROJECT_VIEW, projectId);
    }
    @FXML
    public void EnterToken(ActionEvent event) {
        DialogManager.getInstance().show(Screen.ENTER_TOKEN);
    }

    public void createNewProject(ActionEvent event) {
        DialogManager.getInstance().show(Screen.CREATE_PROJECT);
    }
}