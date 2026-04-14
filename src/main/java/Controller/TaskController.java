package Controller;

import DAO.TaskDAO;
import Service.TaskService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import Enum.Priority;
import Enum.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

import Utils.SceneNavigator;


public class TaskController {
    private TaskService taskService;

//    public TaskController(TaskService taskService) {
//        this.taskService = taskService;
//    }

    @FXML
    private ImageView deadlineicon;

    @FXML
    private ImageView doneicon;

    @FXML
    private ImageView highicon;

    @FXML
    private ImageView inprogressicon;

    @FXML
    private ImageView lowicon;

    @FXML
    private ImageView mediumicon;

    @FXML
    private ImageView nodeadlineicon;

    @FXML
    private ImageView todoicon;
}
