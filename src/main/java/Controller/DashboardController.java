package Controller;

import Cache.DashboardCache;
import DAO.*;
import DTO.ProjectDashboardDTO;
import DTO.TaskDashboardDTO;
import Service.ProjectService;
import Service.TaskService;
import Utils.DialogManager;
import Utils.ScreenManager;
import Utils.UserSession;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import Enum.Screen;

import javafx.event.ActionEvent;

import java.net.URL;
import java.util.*;

import static Service.helper.TaskSearchHelper.normalize;

public class DashboardController {

    private int userId;
    private ProjectService projectService = new ProjectService(new ProjectDAO(), new UserProjectDAO());
    private TaskService taskService = new TaskService(new TaskDAO());
    private Service.NotificationService notificationService = new Service.NotificationService();
    private final URL projectCardFXML = getClass().getResource("/dashboard/dashboardProjectCard.fxml");
    private final URL taskCardFXML = getClass().getResource("/dashboard/dashboardMyTaskCard.fxml");

    private Map<Integer, Node> projectCardMap = new HashMap<>();
    private Map<Integer, Node> taskCardMap = new HashMap<>();


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
    private final long TTL = 5000;



    @FXML
    void initialize(){
        emptyProject.setVisible(false);
        emptyMyTask.setVisible(false);
        showLoading(false);
        userId = UserSession.getUserId();

        if(!cache.getTasks().isEmpty()){
            renderDashboardMyTask(cache.getTasks());
        }

        if(!cache.getProjects().isEmpty()){
            renderDashboardProjects(cache.getProjects());
        }

        loadDashboardData();

        searchBar.textProperty().addListener((obs, oldValue, newValue) -> {
            triggerSearch(newValue);
        });
    }

    void loadDashboardData(){
        long now = System.currentTimeMillis();

        if(now - cache.getLastFetchTime() < TTL) {
            return;
        }

        if(isLoading) return;
        isLoading = true;


        boolean isFirst = cache.getTasks().isEmpty() && cache.getProjects().isEmpty();
        if(isFirst) showLoading(true);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<ProjectDashboardDTO> projects = projectService.getDashboardProjects(userId);
                List<TaskDashboardDTO> tasks = taskService.getDashboardMyTask(userId);

                Platform.runLater(() -> {
                    cache.setData(tasks, projects);
                    notificationService.scanAndSendOverdueEmailsOnly();

                    renderDashboardProjects(projects);
                    renderDashboardMyTask(tasks);

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

    private void renderDashboardProjects(List<ProjectDashboardDTO> projects){
        Set<Integer> currentIds = new HashSet<>();

        for(ProjectDashboardDTO project : projects){
            int projectId = project.getId();
            currentIds.add(projectId);

            if(projectCardMap.containsKey(projectId)){
                Node card = projectCardMap.get(projectId);
                DashboardProjectCardController controller = (DashboardProjectCardController) card.getUserData();

                if(!controller.isSame(project)) {
                    controller.setData(project);
                }
            } else {
                Node card = createDashboardProjectCard(project);
                if(card != null){
                    listActiveProject.getChildren().add(card);
                    projectCardMap.put(projectId, card);
                }
            }
        }

        projectCardMap.entrySet().removeIf(entry ->{
            if(!currentIds.contains(entry.getKey())){
                listActiveProject.getChildren().remove(entry.getValue());
                return true;
            }
            return false;
        });
    }

    private Node createDashboardProjectCard(ProjectDashboardDTO project){
        try {
            FXMLLoader loader = new FXMLLoader(projectCardFXML);
            Node card = loader.load();
            DashboardProjectCardController controller = loader.getController();
            controller.setData(project);
            card.setUserData(controller);
            return card;
        } catch (Exception e)
        { e.printStackTrace();
            return null;
        }
    }

    void renderDashboardMyTask(List<TaskDashboardDTO> tasks){
        Set<Integer> currentIds = new HashSet<>();

        for(TaskDashboardDTO task : tasks){
            int taskId = task.getId();
            currentIds.add(taskId);

            if(taskCardMap.containsKey(taskId)){
                Node card = taskCardMap.get(taskId);
                DashboardMyTaskCardController controller = (DashboardMyTaskCardController) card.getUserData();
                if(!controller.isSame(task))
                    controller.setData(task);
            } else {
                Node card = createDashboardMyTaskCard(task);
                if(card != null){
                    listTask.getChildren().add(card);
                    taskCardMap.put(taskId, card);
                }
            }
        }

        taskCardMap.entrySet().removeIf(entry ->{
            if(!currentIds.contains(entry.getKey())){
                listTask.getChildren().remove(entry.getValue());
                return true;
            }
            return false;
        });
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
        String searchKey = normalize(keyword.toLowerCase().trim());

        for(Node card : projectCardMap.values()){
            DashboardProjectCardController controller = (DashboardProjectCardController) card.getUserData();
            boolean visible = normalize(controller.getProjectName()).contains(keyword);

            card.setVisible(visible);
            card.setManaged(visible);
        }

        for(Node card : taskCardMap.values()){
            DashboardMyTaskCardController controller = (DashboardMyTaskCardController) card.getUserData();
            boolean visible = normalize(controller.getTaskName()).contains(keyword) ||
                    normalize(String.valueOf(controller.getPriority())).contains(keyword) ||
                    normalize(controller.getDeadline()).contains(keyword) ||
                    normalize(controller.getProjectName()).contains(keyword);

            card.setVisible(visible);
            card.setManaged(visible);
        }
        updateEmptyState();
    }
    private void updateEmptyState(){
        boolean hasTask = taskCardMap.values().stream().anyMatch(Node::isVisible);
        emptyMyTask.setVisible(!hasTask);
        emptyMyTask.setManaged(!hasTask);

        boolean hasProject = projectCardMap.values().stream().anyMatch(Node::isVisible);
        emptyProject.setVisible(!hasProject);
        emptyProject.setManaged(!hasProject);
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