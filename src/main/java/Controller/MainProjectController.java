package Controller;

import Enum.TaskStatus;
import Model.Task;
import Service.TaskQueryService;
import Utils.DialogManager;
import Utils.SceneNavigator;
import Utils.ScreenManager;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import Service.helper.TaskUIHelper;
import Service.helper.TaskSearchHelper;
import Enum.Screen;

import java.util.ArrayList;
import java.util.List;

public class MainProjectController {

    // ================= UI =================
    @FXML private VBox vboxTodo;
    @FXML private VBox vboxInProgress;
    @FXML private VBox vboxDone;
    @FXML private TextField searchField;
    @FXML private Hyperlink projectLink, mainProjectLink;

    // ================= SERVICES =================
    private int projectId;
    private TaskQueryService taskService = new TaskQueryService();
    private Timeline searchDelay;

    // Lưu UI hiện tại để diff
    private List<Task> currentTodo = new ArrayList<>();
    private List<Task> currentInProgress = new ArrayList<>();
    private List<Task> currentDone = new ArrayList<>();

    // ================= INIT =================
    public void init(int projectId) {
        this.projectId = 1;
        taskService.init(1);

        if (vboxTodo != null) vboxTodo.setCache(true);
        if (vboxInProgress != null) vboxInProgress.setCache(true);
        if (vboxDone != null) vboxDone.setCache(true);

        // Chuyển logic Drag-Drop sang Helper
        TaskUIHelper.setupColumnDragDrop(vboxTodo, TaskStatus.TODO, taskService, this::loadTasks);
        TaskUIHelper.setupColumnDragDrop(vboxInProgress, TaskStatus.IN_PROGRESS, taskService, this::loadTasks);
        TaskUIHelper.setupColumnDragDrop(vboxDone, TaskStatus.DONE, taskService, this::loadTasks);

        if (searchField != null) {
            searchDelay = new Timeline(new KeyFrame(Duration.millis(300), e -> loadTasks()));
            searchDelay.setCycleCount(1);
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                searchDelay.stop();
                searchDelay.play();
            });
        }

        loadTasks();
        taskService.startRealtimeUpdates(this::loadTasks);
    }

    // ================= LOAD & RENDER =================
    private void loadTasks() {
        List<Task> allTasks = taskService.getCachedTasks();
        String searchKey = searchField.getText();

        // Uỷ quyền sort cho Helper
        List<Task> sortedTasks = TaskSearchHelper.searchAndSort(allTasks, searchKey);

        List<Task> todo = new ArrayList<>();
        List<Task> inProgress = new ArrayList<>();
        List<Task> done = new ArrayList<>();

        for (Task t : sortedTasks) {
            switch (t.getStatus()) {
                case TODO -> todo.add(t);
                case IN_PROGRESS -> inProgress.add(t);
                case DONE -> done.add(t);
            }
        }
        render(todo, inProgress, done);
    }

    private void render(List<Task> todo, List<Task> inProgress, List<Task> done) {
        updateColumn(vboxTodo, currentTodo, todo);
        updateColumn(vboxInProgress, currentInProgress, inProgress);
        updateColumn(vboxDone, currentDone, done);

        currentTodo = new ArrayList<>(todo);
        currentInProgress = new ArrayList<>(inProgress);
        currentDone = new ArrayList<>(done);
    }

    private void updateColumn(VBox column, List<Task> oldList, List<Task> newList) {
        for (int i = 0; i < newList.size(); i++) {
            if (i < column.getChildren().size()) {
                VBox card = (VBox) column.getChildren().get(i);
                Task newTask = newList.get(i);

                // Cập nhật thẻ cũ bằng Controller của nó
                TaskCardController controller = (TaskCardController) card.getProperties().get("controller");
                if (controller != null) {
                    controller.updateUI(newTask, searchField.getText());
                }

                card.setOpacity(1.0);
                card.setScaleX(1.0);
                card.setScaleY(1.0);
            } else {
                // Tạo thẻ mới từ FXML
                VBox card = createCardFromFXML(newList.get(i));
                if (card != null) {
                    card.setOpacity(0);
                    column.getChildren().add(card);

                    FadeTransition fade = new FadeTransition(Duration.millis(150), card);
                    fade.setToValue(1);
                    fade.play();
                }
            }
        }

        if (column.getChildren().size() > newList.size()) {
            column.getChildren().remove(newList.size(), column.getChildren().size());
        }
    }

    private VBox createCardFromFXML(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/task/TaskCard.fxml"));
            VBox card = loader.load();
            TaskCardController controller = loader.getController();
            controller.updateUI(task, searchField.getText());

            // Lưu reference controller vào properties để reuse lúc update in-place
            card.getProperties().put("controller", controller);
            return card;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ================= ACTIONS =================
    @FXML
    private void handleOpenCreateTask(ActionEvent event){
        DialogManager.getInstance().show(Screen.CREATE_TASK);

    }

    public void handleProject(ActionEvent event) {
        ScreenManager.getInstance().show(Screen.ALL_MY_PROJECT);
    }

}