package Controller;

import Service.InviteService;
import DAO.EmailInviteDAO;
import DAO.InviteLinkDAO;
import DAO.JoinRequestDAO;
import DAO.UserProjectDAO;

// Nhớ import thêm 3 dòng này để chuyển màn hình
import Enum.Screen;
import Utils.DialogManager;
import Utils.ScreenManager;
import Utils.UserSession;

import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class TokenController {

    @FXML private TextField txtToken;
    @FXML private Button btnJoin,close;
    @FXML private Label lblMessage;
    @FXML
    private StackPane overlay;

    private InviteService inviteService;
    @FXML
    private ProgressIndicator loading;
    private int currentUserId;
    private String currentUserEmail;

    @FXML
    public void initialize() {
        currentUserId = UserSession.getUserId();
        currentUserEmail = UserSession.getEmail();
        loading.setVisible(false);
        inviteService = new InviteService(new InviteLinkDAO(), new EmailInviteDAO(), new JoinRequestDAO(), new UserProjectDAO());
        btnJoin.setOnAction(event -> handleJoinProject());
    }

    private void handleJoinProject() {
        String token = txtToken.getText().trim();

        if (token.isEmpty()) {
            showInlineMessage("Vui lòng nhập mã tham gia từ email!", false);
            return;
        }

        loading.setVisible(true);
        btnJoin.setDisable(true);
        loading.setProgress(-1);
        btnJoin.setText("");

        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                return inviteService.acceptEmailInvite(token, currentUserId, currentUserEmail);
            }
        };

        task.setOnSucceeded(event -> {
            int joinedProjectId = task.getValue();

            showInlineMessage("Thành công! Đang chuyển hướng...", true);
            txtToken.clear();

            ScreenManager.getInstance()
                    .show(Screen.MAIN_PROJECT_VIEW, joinedProjectId);
        });

        task.setOnFailed(event -> {
            loading.setVisible(false);
            btnJoin.setDisable(false);
            btnJoin.setText("Tham gia dự án");
            Throwable e = task.getException();
            showInlineMessage(e.getMessage(), false);
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

    }

    private void showInlineMessage(String message, boolean isSuccess) {
        lblMessage.setText(message);
        if (isSuccess) {
            lblMessage.setStyle("-fx-text-fill: #007700; -fx-font-weight: bold;");
        } else {
            lblMessage.setStyle("-fx-text-fill: #FF0000; -fx-font-weight: bold;");
        }
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> lblMessage.setText(""));
        pause.play();
    }

    @FXML
    private void handleCancel(javafx.event.ActionEvent event) {
        DialogManager.getInstance().close(overlay);
    }
}