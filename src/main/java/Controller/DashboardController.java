package Controller;

import Cache.DashboardCache;
import Cache.ProjectCache;
import DAO.*;
import DTO.ProjectDashboardDTO;
import DTO.TaskDashboardDTO;
import Service.ProjectService;
import Service.TaskService;
import Utils.DialogManager;
import Utils.ScreenManager;
import Utils.UserSession;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import Enum.Screen;

import javafx.event.ActionEvent;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

import static Service.helper.TaskSearchHelper.normalize;

public class DashboardController {

    private int userId;
    private ProjectService projectService = new ProjectService(new ProjectDAO(), new UserProjectDAO());
    private TaskService taskService = new TaskService(new TaskDAO());
    private Service.NotificationService notificationService = new Service.NotificationService();
    private final URL projectCardFXML = getClass().getResource("/project/projectcard.fxml");
    private final URL taskCardFXML = getClass().getResource("/dashboard/dashboardMyTaskCard.fxml");

    private List<ProjectDashboardDTO> allProjects = new ArrayList<>();
    private List<TaskDashboardDTO> allTasks = new ArrayList<>();

    private Timeline sortDelay;
    private Timeline searchDelay;


    @FXML
    private Button buttonCreateProject;

    @FXML
    private Label deadline;

    @FXML
    private Label description;

    @FXML
    private Label invitation;

    @FXML
    private HBox listActiveProject;

    @FXML
    private VBox listNotification;

    @FXML
    private VBox listTask;

    @FXML
    private TextField searchBar;

    @FXML
    private Label showAllTask;

    @FXML
    private Label taskAssinged;

    @FXML
    private Label welcome;

    @FXML
    private ProgressIndicator loading;

    @FXML
    private VBox emptyMyTask;

    @FXML
    private VBox emptyProject;
    private boolean isLoading = false;

    private DashboardCache cache = DashboardCache.getInstance();
    private ProjectCache projectCache = ProjectCache.getInstance();



    @FXML
    void initialize(){
        emptyProject.setVisible(false);
        emptyMyTask.setVisible(false);
        showLoading(false);
        updateEmptyState();
        userId = UserSession.getUserId();

        setupSearch();

        if (!cache.getTasks().isEmpty() || !projectCache.getAll().isEmpty()) {
            applyData(projectCache.getAll(), cache.getTasks());
        }

        loadDashboardData();
    }

    private void applyData(List<ProjectDashboardDTO> projects, List<TaskDashboardDTO> tasks) {
        allProjects = new ArrayList<>(projects);
        allTasks = new ArrayList<>(tasks);

        scheduleRender();
    }

    void loadDashboardData(){
        if(isLoading) return;
        isLoading = true;


        boolean isFirst = cache.getTasks().isEmpty() && projectCache.getAll().isEmpty();
        if(isFirst) showLoading(true);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<ProjectDashboardDTO> projects = projectService.getAllMyProjects(userId);
                List<TaskDashboardDTO> tasks = taskService.getDashboardMyTask(userId);
                notificationService.scanAndSendOverdueEmailsOnly();

                Platform.runLater(() -> {
                    cache.setData(tasks);
                    projectCache.putList(projects);

                    applyData(projects, tasks);

                    updateEmptyState();
                    showLoading(false);
                    isLoading = false;
                });
                return null;
            }
        };
        new Thread(task).start();
    }

    private void showLoading(boolean b){
        loading.setVisible(b);
        loading.setManaged(b);
        if(b) loading.setProgress(-1);
    }

    private void setupSearch() {
        searchDelay = new Timeline(
                new KeyFrame(Duration.millis(300), e -> triggerSearch(searchBar.getText()))
        );
        searchDelay.setCycleCount(1);

        searchBar.textProperty().addListener((obs, oldVal, newVal) -> {
            searchDelay.stop();
            searchDelay.play();
        });
    }

    private Node createDashboardProjectCard(ProjectDashboardDTO project){
        try {
            FXMLLoader loader = new FXMLLoader(projectCardFXML);
            Node card = loader.load();
            ProjectCardController controller = loader.getController();
            controller.setProjectData(project);
            card.setUserData(controller);
            return card;
        } catch (Exception e)
        { e.printStackTrace();
            return null;
        }
    }

    private Node createDashboardMyTaskCard(TaskDashboardDTO task){
        try {
            FXMLLoader loader = new FXMLLoader(taskCardFXML);
            Node card = loader.load();
            DashboardMyTaskCardController controller = loader.getController();
            controller.setData(task);
            card.setUserData(controller);
            return card;
        } catch (Exception e)
        { e.printStackTrace();
            return null;
        }
    }

    private void triggerSearch(String keyword){
        String searchKey = normalize(keyword.trim());

        for(Node node : listActiveProject.getChildren()){
            ProjectCardController controller = (ProjectCardController) node.getUserData();
            boolean visible = normalize(controller.getProjectName()).contains(searchKey) ||
                    normalize(controller.getOwnerName()).contains(searchKey);

            node.setVisible(visible);
            node.setManaged(visible);
        }

        for(Node node : listTask.getChildren()){
            DashboardMyTaskCardController controller = (DashboardMyTaskCardController) node.getUserData();
            boolean visible = normalize(controller.getTaskName()).contains(searchKey) ||
                    normalize(String.valueOf(controller.getPriority())).contains(searchKey) ||
                    normalize(controller.getDeadline()).contains(searchKey) ||
                    normalize(controller.getProjectName()).contains(searchKey);

            node.setVisible(visible);
            node.setManaged(visible);
        }
        updateEmptyState();
    }
    private void updateEmptyState(){
        boolean hasTask = listTask.getChildren().stream().anyMatch(Node::isVisible);
        emptyMyTask.setVisible(!hasTask);
        emptyMyTask.setManaged(!hasTask);

        boolean hasProject = listActiveProject.getChildren().stream().anyMatch(Node::isVisible);
        emptyProject.setVisible(!hasProject);
        emptyProject.setManaged(!hasProject);
    }

    private void renderUI(){
        // 🔥 SORT PROJECT
        allProjects = projectService.sortByScore(new ArrayList<>(allProjects));

        List<Node> projectNodes = new ArrayList<>();
        for (ProjectDashboardDTO dto : allProjects) {
            Node node = createDashboardProjectCard(dto);
            if (node != null) projectNodes.add(node);
        }
        listActiveProject.getChildren().setAll(projectNodes);

        // 🔥 TASK: giữ nguyên DB
        List<Node> taskNodes = new ArrayList<>();
        for (TaskDashboardDTO dto : allTasks) {
            Node node = createDashboardMyTaskCard(dto);
            if (node != null) taskNodes.add(node);
        }
        listTask.getChildren().setAll(taskNodes);

        triggerSearch(searchBar.getText());
        updateEmptyState();
    }

    private void scheduleRender() {
        if (sortDelay == null) {
            sortDelay = new Timeline(
                    new KeyFrame(Duration.millis(200), e -> renderUI())
            );
            sortDelay.setCycleCount(1);
        }
        sortDelay.stop();
        sortDelay.play();
    }

    @FXML
    void goToAllMyProjects(MouseEvent event) {
        ScreenManager.getInstance().show(Screen.ALL_MY_PROJECT);
    }

    @FXML
    void createProject(ActionEvent event) {
        DialogManager.getInstance().show(Screen.CREATE_PROJECT);
    }

}