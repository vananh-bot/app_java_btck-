package Controller;

import DAO.TaskDAO;
import Enum.Priority;
import Enum.TaskStatus;
import Service.TaskService;
import Utils.DataReceiver;
import Utils.DialogManager;
import Utils.SceneNavigator;
import Utils.ScreenManager;
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

    private int currentProjectId;

    @Override
    public void initData(Integer projectId){
        this.currentProjectId = projectId;
    }

    private ToggleGroup statusGroup;
    private ToggleGroup priorityGroup;

    public void initialize(){
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

        try {
            TaskService service = new TaskService(new TaskDAO());
            int taskId = service.createTask(taskTitle, taskDescription, taskPriority, taskStatus, taskDeadline, currentProjectId);

            ScreenManager.getInstance().show(Screen.TASK_DETAILS, taskId);

        } catch (IllegalArgumentException e){
            error.setVisible(true);
            error.setStyle("-fx-text-fill: #ff0000;");
            error.setText(e.getMessage());
        }
        catch(Exception e){
            e.printStackTrace();
            error.setVisible(true);
            error.setText("Lỗi hệ thống!");
        }
    }

    @FXML
    void cancelAddTask(ActionEvent event) {
        DialogManager.getInstance().close(overlay);
    }

}