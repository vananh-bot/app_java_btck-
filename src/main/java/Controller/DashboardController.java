package Controller;

import DAO.*;
import Model.Project;
import Model.ProjectDashboardDTO;
import Model.TaskDashboardDTO;
import Service.ProjectService;
import Service.TaskService;
import Utils.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.util.List;

public class DashboardController {

    private int userId = UserSession.getUserId();
    private ProjectService projectService = new ProjectService(new ProjectDAO(), new UserProjectDAO(), new InviteDAO(), new TaskDAO());
    private TaskService taskService = new TaskService(new TaskDAO(), new TaskAssignmentDAO());

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
    void initialize(){
        loadDashboardProjects();
        loadDashboardMyTasks();
    }

    void loadDashboardProjects(){
        userId = 38;

        new Thread(() -> {

            List<ProjectDashboardDTO> projects = projectService.getDashboardProjects(userId);


            Platform.runLater(() -> {
                renderDashboardProjects(projects);
            });

        }).start();
    }

    private void renderDashboardProjects(List<ProjectDashboardDTO> projects){
        listActiveProject.getChildren().clear();

        for(ProjectDashboardDTO project : projects){
            Node card = createDashboardProjectCard(project);
            if(card != null){
                listActiveProject.getChildren().add(card);
            }
        }
    }

    private Node createDashboardProjectCard(ProjectDashboardDTO project){
        try {
            FXMLLoader loader = new FXMLLoader( getClass().getResource("/dashboard/dashboardProjectCard.fxml") );
            Node card = loader.load();
            DashboardProjectCardController controller = loader.getController();
            controller.setData(project);
            return card;
        } catch (Exception e)
        { e.printStackTrace();
            return null;
        }
    }

    void loadDashboardMyTasks(){
        userId = 38;

        new Thread(() -> {

            List<TaskDashboardDTO> tasks = taskService.getDashboardMyTask(userId);


            Platform.runLater(() -> {
                renderDashboardMyTask(tasks);
            });

        }).start();
    }

    void renderDashboardMyTask(List<TaskDashboardDTO> tasks){
        listTask.getChildren().clear();

        for(TaskDashboardDTO task : tasks){
            Node card = createDashboardMyTaskCard(task);
            if(card != null){
                listTask.getChildren().add(card);
            }
        }
    }

    private Node createDashboardMyTaskCard(TaskDashboardDTO task){
        try {
            FXMLLoader loader = new FXMLLoader( getClass().getResource("/dashboard/dashboardMyTaskCard.fxml") );
            Node card = loader.load();
            DashboardMyTaskCardController controller = loader.getController();
            controller.setData(task);
            return card;
        } catch (Exception e)
        { e.printStackTrace();
            return null;
        }
    }

    @FXML
    void goToAllMyProjects(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/project/allMyProject.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("AllMyProject");
            stage.show();
        } catch (Exception e) {
            System.out.println("Lỗi chuyển màn hình: " + e.getMessage());
        }
    }

    @FXML
    void createProject(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/project/createProject.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CreateProject");
            stage.show();
        } catch (Exception e) {
            System.out.println("Lỗi chuyển màn hình: " + e.getMessage());
        }
    }

}