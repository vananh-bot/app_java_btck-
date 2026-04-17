package Service.helper;

import Service.TaskQueryService;
import Enum.TaskStatus;
import Model.Task;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.input.TransferMode;

import java.security.Provider;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TaskUIHelper {

    public static void applyDeadlineStyle(VBox card, Task task) {
        // 1. Xóa sạch các màu deadline cũ trước khi tính toán lại
        card.getStyleClass().removeAll("deadline-overdue", "deadline-soon", "deadline-safe");

        // Nếu task không có hạn chót -> Giữ nguyên bóng mờ xám mặc định
        if (task.getDeadline() == null) return;

        // 2. Tính số ngày còn lại
        long days = ChronoUnit.DAYS.between(LocalDate.now(), task.getDeadline().toLocalDate());

        // 3. Phân loại để bật màu
        if (days < 0) {
            // Đã quá hạn -> Bật viền ĐỎ
            card.getStyleClass().add("deadline-overdue");

        } else if (days <= 2) { // Bạn có thể chỉnh số 2 này (ví dụ còn <= 2 ngày là báo động Cam)
            // Gần đến hạn -> Bật viền CAM
            card.getStyleClass().add("deadline-soon");

        } else {
            // Còn lâu mới đến hạn -> Bật viền XANH LÁ
            card.getStyleClass().add("deadline-safe");
        }
    }

    public static String calculateDaysRemaining(LocalDateTime deadlineDateTime) {
        if (deadlineDateTime == null) return "Không có hạn";
        LocalDate deadlineDate = deadlineDateTime.toLocalDate();
        LocalDate today = LocalDate.now();
        long daysBetween = ChronoUnit.DAYS.between(today, deadlineDate);

        if (daysBetween == 0) return "Hôm nay";
        if (daysBetween > 0) return daysBetween + " ngày nữa";
        return "Quá hạn " + Math.abs(daysBetween) + " ngày";
    }

    public static TextFlow highlightText(String originalText, String keyword, boolean isDesc) {
        TextFlow textFlow = new TextFlow();
        if (originalText == null) originalText = "";

        String normKeyword = TaskSearchHelper.normalize(keyword);
        String[] words = normKeyword.split("\\s+");
        List<String> searchWords = new ArrayList<>();
        for (String w : words) {
            if (!w.isEmpty()) searchWords.add(w);
        }

        if (searchWords.isEmpty()) {
            Text t = new Text(originalText);
            t.getStyleClass().add(isDesc ? "description" : "task-title");
            textFlow.getChildren().add(t);
            return textFlow;
        }

        String normText = TaskSearchHelper.normalize(originalText);
        int currentIndex = 0;

        while (currentIndex < originalText.length()) {
            int bestMatchIndex = -1;
            String bestMatchWord = "";

            for (String word : searchWords) {
                int index = normText.indexOf(word, currentIndex);
                if (index != -1 && (bestMatchIndex == -1 || index < bestMatchIndex)) {
                    bestMatchIndex = index;
                    bestMatchWord = word;
                }
            }

            if (bestMatchIndex != -1) {
                if (bestMatchIndex > currentIndex) {
                    Text tNormal = new Text(originalText.substring(currentIndex, bestMatchIndex));
                    tNormal.getStyleClass().add(isDesc ? "description" : "task-title");
                    textFlow.getChildren().add(tNormal);
                }

                Text tHighlight = new Text(originalText.substring(bestMatchIndex, bestMatchIndex + bestMatchWord.length()));
                tHighlight.getStyleClass().add(isDesc ? "description" : "task-title");
                tHighlight.getStyleClass().add("highlight");
                textFlow.getChildren().add(tHighlight);

                currentIndex = bestMatchIndex + bestMatchWord.length();
            } else {
                Text tRest = new Text(originalText.substring(currentIndex));
                tRest.getStyleClass().add(isDesc ? "description" : "task-title");
                textFlow.getChildren().add(tRest);
                break;
            }
        }
        return textFlow;
    }

    public static void setupColumnDragDrop(VBox column, TaskStatus status, TaskQueryService service, Runnable onTaskMoved) {
        column.setOnDragOver(e -> {
            if (e.getGestureSource() != column) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

        column.setOnDragEntered(e -> column.getStyleClass().add("column-drag-over"));
        column.setOnDragExited(e -> column.getStyleClass().remove("column-drag-over"));

        column.setOnDragDropped(e -> {
            VBox card = (VBox) e.getGestureSource();
            Task task = (Task) card.getUserData();

            if (task.getStatus() == status) {
                e.setDropCompleted(false);
                e.consume();
                return;
            }

            task.setStatus(status);
            onTaskMoved.run(); // Yêu cầu UI sắp xếp lại cực mượt từ RAM
            service.updateTaskStatusAsync(task.getId(), status); // Cập nhật ngầm

            e.setDropCompleted(true);
            e.consume();
        });
    }
}