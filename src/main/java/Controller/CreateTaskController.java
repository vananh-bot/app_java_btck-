package Controller;

import DAO.TaskDAO;
import Enum.Priority;
import Enum.TaskStatus;
import Service.TaskService;
import Utils.DataReceiver;
import Utils.DialogManager;
import Utils.SceneNavigator;
import Utils.ScreenManager;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.time.LocalDate;
import java.time.LocalDateTime;
import Enum.Screen;


public class CreateTaskController implements DataReceiver<Integer> {
    private TaskService taskService;

    @FXML
    private StackPane overlay;

    @FXML
    private Label error;
    @FXML
    private ToggleButton btnAddTask;
    @FXML
    private ToggleButton btncancel;
    @FXML
    private DatePicker btndeadline;
    @FXML
    private ToggleButton btnhigh, btnlow, btnmedium;
    @FXML
    private ToggleButton btntodo, btninprogress, btndone;
    @FXML
    private TextArea description;
    @FXML
    private TextArea title;
    @FXML
    private ProgressIndicator loading;

    private int currentProjectId;

    @Override
    public void initData(Integer projectId){
        this.currentProjectId = projectId;
    }

    private ToggleGroup statusGroup;
    private ToggleGroup priorityGroup;

    public void initialize(){
        loading.setVisible(false);

        //khởi tạo status group
        statusGroup = new ToggleGroup();
        btntodo.setToggleGroup(statusGroup);
        btninprogress.setToggleGroup(statusGroup);
        btndone.setToggleGroup(statusGroup);
        btntodo.setSelected(true);

        statusGroup.selectedToggleProperty().addListener((obs, oldToogle, newToggle) -> {
            if(newToggle == null){
                statusGroup.selectToggle(oldToogle);
            }
        });

        //khởi tạo priority group
        priorityGroup = new ToggleGroup();
        btnlow.setToggleGroup(priorityGroup);
        btnhigh.setToggleGroup(priorityGroup);
        btnmedium.setToggleGroup(priorityGroup);
        btnlow.setSelected(true);

        priorityGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if(newToggle == null){
                priorityGroup.selectToggle(oldToggle);
            }
        });

        //set Userdata
        btnlow.setUserData(Priority.LOW);
        btnmedium.setUserData(Priority.MEDIUM);
        btnhigh.setUserData(Priority.HIGH);

        btntodo.setUserData(TaskStatus.TODO);
        btninprogress.setUserData(TaskStatus.IN_PROGRESS);
        btndone.setUserData(TaskStatus.DONE);

        //khong cho chon ngay qua khu
        btndeadline.setDayCellFactory(datePicker -> new DateCell(){
            @Override
            public void updateItem (LocalDate date, boolean empty){
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        // khong cho nhap deadline, mac dinh deadline la ngay hom nay
        btndeadline.setEditable(false);
        btndeadline.setValue(LocalDate.now());
    }

    @FXML
    void addTask(ActionEvent event) {
        String taskTitle = title.getText().trim();
        String taskDescription = description.getText().trim();
        Priority taskPriority = (Priority) priorityGroup.getSelectedToggle().getUserData();
        TaskStatus taskStatus = (TaskStatus) statusGroup.getSelectedToggle().getUserData();
        LocalDate date = btndeadline.getValue();
        LocalDateTime taskDeadline = date.atStartOfDay();

        loading.setVisible(true);
        btnAddTask.setDisable(true);
        loading.setProgress(-1);
        btnAddTask.setText("");

        TaskService service = new TaskService(new TaskDAO());

        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                return service.createTask(taskTitle, taskDescription, taskPriority, taskStatus, taskDeadline, currentProjectId);
            }
        };

        task.setOnSucceeded(e -> {
            loading.setVisible(false);
            btnAddTask.setDisable(false);
            btnAddTask.setText("Tạo công việc");

            int taskId = task.getValue();
            ScreenManager.getInstance().show(Screen.TASK_DETAILS, taskId);
        });

        task.setOnFailed(e -> {
            loading.setVisible(false);
            btnAddTask.setDisable(false);
            btnAddTask.setText("Tạo công việc");

            Throwable ex = task.getException();
            if(ex instanceof IllegalArgumentException) {
                error.setVisible(true);
                error.setStyle("-fx-text-fill: #ff0000;");
                error.setText(ex.getMessage());
            } else {
                error.setVisible(true);
                error.setText("Lỗi hệ thống!");
            }
        });
    }

    @FXML
    void cancelAddTask(ActionEvent event) {
        DialogManager.getInstance().close(overlay);
    }

}