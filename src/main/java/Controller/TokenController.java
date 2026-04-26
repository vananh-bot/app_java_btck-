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
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    public void initialize() {
        inviteService = new InviteService(new InviteLinkDAO(), new EmailInviteDAO(), new JoinRequestDAO(), new UserProjectDAO());
        btnJoin.setOnAction(event -> handleJoinProject());
    }

    private void handleJoinProject() {
        String token = txtToken.getText().trim();

        if (token.isEmpty()) {
            showInlineMessage("Vui lòng nhập mã tham gia từ email!", false);
            return;
        }

        int currentUserId = UserSession.getUserId();
        String currentUserEmail = UserSession.getEmail();
        // Tạm hardcode để test, nhớ đổi thành getEmail() từ Session thực tế nhé
//        int currentUserId = 38;
//        String currentUserEmail = "hahoaiphuong07012006@gmail.com";

        btnJoin.setDisable(true);
        btnJoin.setText("Đang kiểm tra...");

        try {
            // Gọi Service (Nếu có lỗi, nó sẽ văng thẳng xuống block catch ở dưới)
            int joinedProjectId = inviteService.acceptEmailInvite(token, currentUserId, currentUserEmail);

            // Nếu chạy được đến dòng này nghĩa là TẤT CẢ ĐỀU ĐÚNG
            showInlineMessage("Thành công! Đang chuyển hướng...", true);
            txtToken.clear();

            //UserSession.setCurrentProjectId(joinedProjectId);
            ScreenManager.getInstance().show(Screen.MAIN_PROJECT_VIEW,joinedProjectId);

        } catch (Exception e) {
            // CỰC KỲ VI DIỆU: e.getMessage() chính là câu tiếng Việt bạn viết ở Tầng Service!
            showInlineMessage(e.getMessage(), false);

        } finally {
            btnJoin.setDisable(false);
            btnJoin.setText("Tham gia dự án");
        }
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