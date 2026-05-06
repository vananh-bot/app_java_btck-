package Controller;

import Cache.ProjectCache;
import DTO.ProjectDashboardDTO;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.text.Normalizer;
import java.util.*;
import Enum.Screen;

import static Service.helper.TaskSearchHelper.normalize;

public class AllProjectController implements Initializable {

    @FXML private FlowPane projectContainer;
    @FXML private TextField searchInput;
    @FXML private Button createProject;
    @FXML
    private ProgressIndicator loading;

    @FXML
    private VBox emptyProject;

    private ProjectService projectService;

    // Timer cho Search và Sort
    private Timeline searchDelay;
    private Timeline sortDelay;

    // Lưu trữ trạng thái UI
    private Map<Integer, ProjectDashboardDTO> oldDataMap = new HashMap<>();
    private Map<Integer, AnchorPane> projectCardMap = new HashMap<>();

    private ProjectCache projectCache = ProjectCache.getInstance();

    private List<ProjectDashboardDTO> allProjectDTOs = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showLoading(false);
        emptyProject.setVisible(false);
        projectService = new ProjectService(new ProjectDAO(), new UserProjectDAO());

        setupSearchLogic();


            int currentUserId = UserSession.getUserId();
            if(currentUserId == -1) return;

            if(!projectCache.getAll().isEmpty()) {
                applyDeltaUISafely(projectCache.getAll());
            }

            loadData(currentUserId);

    }

    private void loadData(int userId) {

        //first load
        if(projectCache.getAll().isEmpty())
            showLoading(true);

        new Thread(() -> {
            try {
//                long startTime = System.currentTimeMillis();
//                List<ProjectDashboardDTO> newList = projectService.getAllMyProjects(userId);
//                long endTime = System.currentTimeMillis();
//
//                System.out.println("Đã tải " + newList.size() + " dự án. Thời gian DB: " + (endTime - startTime) + "ms");

                List<ProjectDashboardDTO> newList = projectService.getAllMyProjects(userId);

                Platform.runLater(() -> {
                    applyDeltaUISafely(newList);
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

    private void applyDeltaUISafely(List<ProjectDashboardDTO> list) {
        Map<Integer, ProjectDashboardDTO> newMap = new HashMap<>();
        for (ProjectDashboardDTO dto : list) {
            newMap.put(dto.getId(), dto);
            projectCache.put(dto);
        }
        allProjectDTOs = new ArrayList<>(list);


        try {
            // 1. Update / Add
            for (Map.Entry<Integer, ProjectDashboardDTO> entry : newMap.entrySet()) {
                int id = entry.getKey();
                ProjectDashboardDTO newDto = entry.getValue();
                ProjectDashboardDTO oldDto = oldDataMap.get(id);

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
        for (ProjectDashboardDTO dto : projectCache.getAll()) {
            if (normalize(dto.getName()).contains(keyword)) {
                visibleIds.add(dto.getId());
            }
        }
        for (Map.Entry<Integer, AnchorPane> entry : projectCardMap.entrySet()) {
            boolean visible = visibleIds.contains(entry.getKey());
            entry.getValue().setVisible(visible);
            entry.getValue().setManaged(visible);
        }
        updateEmptyState();
    }

    /* =========================================
     * LOGIC SẮP XẾP ĐÃ ĐƯỢC KHÔI PHỤC
     * ========================================= */
    private void sortUI() {
        allProjectDTOs = projectService.sortByScore(new ArrayList<>(allProjectDTOs));
        List<AnchorPane> newOrder = new ArrayList<>();
        for (ProjectDashboardDTO dto : allProjectDTOs) {
            AnchorPane card = projectCardMap.get(dto.getId());
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
    private boolean isChanged(ProjectDashboardDTO oldDto, ProjectDashboardDTO newDto) {
        return !Objects.equals(oldDto.getName(), newDto.getName())
//                || !Objects.equals(oldDto.getProject().getDescription(), newDto.getProject().getDescription())
                || oldDto.getToDoCount() != newDto.getToDoCount()
                || oldDto.getInProgressCount() != newDto.getInProgressCount()
                || oldDto.getDoneCount() != newDto.getDoneCount()
                || !Objects.equals(oldDto.getOwnerName(), newDto.getOwnerName());
    }

    private AnchorPane createProjectCard(ProjectDashboardDTO dto) throws Exception {
        URL fxmlLocation = getClass().getResource("/project/projectcard.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        AnchorPane card = loader.load();

        ProjectCardController controller = loader.getController();
        if (controller != null) {
            controller.setProjectData(dto);
        }

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
    @FXML
    public void EnterToken(ActionEvent event) {
        DialogManager.getInstance().show(Screen.ENTER_TOKEN);
    }

    public void createNewProject(ActionEvent event) {
        DialogManager.getInstance().show(Screen.CREATE_PROJECT);
    }
}