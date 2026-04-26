package Controller;

import Service.InviteService;
import DAO.EmailInviteDAO;
import DAO.InviteLinkDAO;
import DAO.JoinRequestDAO;
import DAO.UserProjectDAO;
import Enum.JoinMode;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.Duration;

public class ShareLinkController {

    // Ánh xạ các UI Component từ FXML
    @FXML private TextField txtInviteLink;
    @FXML private Button btnCopy;

    @FXML private ToggleGroup yg; // Nhóm chứa 2 chế độ
    @FXML private ToggleButton togglePublic;
    @FXML private ToggleButton toggleApproval;

    @FXML private Button btnGenerateLink;
    @FXML private Hyperlink linkToEmail;
    @FXML private Label lbl;

    // Khởi tạo Service
    private InviteService inviteService;

    // Giả sử ID của Project hiện tại đang mở là 1 (sau này bạn truyền từ màn hình chính sang)
    private int currentProjectId = 1;

    // Domain gốc của ứng dụng (để nối với token tạo thành link hoàn chỉnh)
    private final String BASE_URL = "https://arc-workspace.io/invite?token=";

    @FXML
    public void initialize() {
        // Khởi tạo các DAO và Service (giống hệt bên EmailInviteController)
        inviteService = new InviteService(
                new InviteLinkDAO(),
                new EmailInviteDAO(),
                new JoinRequestDAO(),
                new UserProjectDAO()
        );

        // Đặt UserData cho các Toggle để dễ dàng lấy ra Enum tương ứng
        togglePublic.setUserData(JoinMode.PUBLIC);
        toggleApproval.setUserData(JoinMode.APPROVAL_REQUIRED);

        // Mặc định chọn chế độ "Công khai" khi vừa mở màn hình
        togglePublic.setSelected(true);
        txtInviteLink.setEditable(false); // Không cho người dùng gõ vào ô link

        // Gắn sự kiện cho các nút bấm
        btnGenerateLink.setOnAction(event -> handleGenerateLink());
        btnCopy.setOnAction(event -> handleCopyLink());
        linkToEmail.setOnAction(event -> handleSwitchToEmail());
    }

    // Xử lý sự kiện: Bấm nút "Tạo liên kết mới"
    private void handleGenerateLink() {
        // 1. Lấy chế độ người dùng đang chọn (Công khai hay Phê duyệt)
        ToggleButton selectedToggle = (ToggleButton) yg.getSelectedToggle();
        if (selectedToggle == null) return;

        JoinMode selectedMode = (JoinMode) selectedToggle.getUserData();

        // 2. Gọi Service để vô hiệu hóa link cũ và tạo token mới
        try {
            String newToken = inviteService.createInviteLink(currentProjectId, selectedMode);

            // 3. Hiển thị link mới lên TextField
            String fullInviteUrl = BASE_URL + newToken;
            txtInviteLink.setText(fullInviteUrl);

            showInlineMessage("Đã tạo liên kết mới thành công!", true);

        } catch (Exception e) {
            showInlineMessage("Không thể tạo liên kết. Vui lòng thử lại!", false);
            e.printStackTrace();
        }
    }

    // Xử lý sự kiện: Bấm nút "Sao chép liên kết"
    private void handleCopyLink() {
        String currentLink = txtInviteLink.getText();

        if (currentLink == null || currentLink.isEmpty()) {
            showInlineMessage("Vui lòng tạo liên kết trước khi sao chép.", false);
            return;
        }

        // Lấy Clipboard của hệ điều hành và lưu chuỗi vào đó
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(currentLink);
        clipboard.setContent(content);

        // Thông báo cho người dùng
//        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã sao chép liên kết vào khay nhớ tạm!");
        showInlineMessage("Đã tạo liên kết thành công!",true);
    }

    // Xử lý sự kiện: Bấm "Quay lại mời qua email"
    private void handleSwitchToEmail() {
        System.out.println("Chuyển màn hình sang EmailInviteView.fxml...");
        // TODO: Viết logic đổi Scene JavaFX của bạn ở đây để chuyển lại màn hình Email
    }

    // Hàm tiện ích hiện Popup thông báo
//    private void showAlert(Alert.AlertType type, String title, String content) {
//        Alert alert = new Alert(type);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(content);
//        alert.showAndWait();
//    }
    private void showInlineMessage(String message, boolean isSuccess) {
        lbl.setText(message);

        // Đổi màu chữ tùy theo thành công hay thất bại
        if (isSuccess) {
            lbl.setStyle("-fx-text-fill: #007700; -fx-font-size: 13px; -fx-font-weight: bold;"); // Màu xanh lá
        } else {
            lbl.setStyle("-fx-text-fill: #FF0000; -fx-font-size: 13px; -fx-font-weight: bold;"); // Màu đỏ
        }

        // Tự động xóa dòng chữ sau 3 giây để giao diện gọn gàng lại
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> lbl.setText(""));
        pause.play();
    }
}