package Controller;

import Model.TaskDashboardDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import Enum.Priority;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DashboardMyTaskCardController {
    @FXML
    private Label datetime;

    @FXML
    private Label nameOfTask;

    @FXML
    private Label priority;

    @FXML
    private VBox task;

    private int taskId;

    void setData(TaskDashboardDTO task){
        taskId = task.getId();

        nameOfTask.setText(task.getTitle());
        Priority priority1 = task.getPriority();
        switch (priority1){
            case HIGH:
                priority.setText("* ƯU TIÊN CAO");
                priority.getStyleClass().add("priority-high");
                break;
            case MEDIUM:
                priority.setText("* ƯU TIÊN TRUNG BÌNH");
                priority.getStyleClass().add("priority-medium");
                break;
            case LOW:
                priority.setText("* ƯU TIÊN THẤP");
                priority.getStyleClass().add("priority-low");
                break;
        }
        LocalDateTime deadline = task.getDeadline();
        LocalDateTime now = LocalDateTime.now();

        long days = ChronoUnit.DAYS.between(now, deadline);

        if(days < 0){
            datetime.setText("Đã quá hạn " + Math.abs(days) + " ngày");
            datetime.getStyleClass().add("deadline-overdue");
        } else if(days == 0){
            datetime.setText("Hạn hôm nay");
            datetime.getStyleClass().add("deadline-overdue");
        }
        else if(days <= 7){
            datetime.setText("Còn " + days + " ngày");
            datetime.getStyleClass().add("deadline-soon");
        }
        else{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            datetime.setText(deadline.format(formatter));
        }

    }
    @FXML
    void goToTaskDetails(MouseEvent event) {
//        try {
//            FXMLLoader loader = new FXMLLoader(
//                    getClass().getResource("/task/taskdetails.fxml")
//            );
//
//            Parent view = loader.load();
//
//            TaskController controller = loader.getController();
//
//            // 👉 truyền ID
//            controller.setTaskId(taskId);
//
//            Stage stage = (Stage) projectCard.getScene().getWindow();
//            stage.getScene().setRoot(view);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

            System.out.println("Fake open taskId: " + taskId);
    }

}
