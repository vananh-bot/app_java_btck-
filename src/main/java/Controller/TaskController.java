package Controller;

import Service.TaskService;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.ImageView;


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

    @FXML
    void deadlinetask(MouseEvent event) {

    }

    @FXML
    void donetask(MouseEvent event) {

    }

    @FXML
    void hightask(MouseEvent event) {

    }

    @FXML
    void inprogresstask(MouseEvent event) {

    }

    @FXML
    void lowtask(MouseEvent event) {

    }

    @FXML
    void mediumtask(MouseEvent event) {

    }

    @FXML
    void nodeadline(MouseEvent event) {

    }

    @FXML
    void todotask(MouseEvent event) {

    }

    public void createTask() {

    }

    public void assignTask() {

    }

    public void updateTask() {

    }

    public void deleteTask() {

    }
}
