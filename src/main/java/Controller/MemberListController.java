package Controller;

import DTO.MemberDTO;
import Service.MemberService;
import Utils.DataReceiver;
import Utils.DialogManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;
import Enum.Screen;

public class MemberListController implements DataReceiver<Integer> {

    @FXML private VBox memberContainer;
    @FXML private TextField txtSearch;
    @FXML
    private StackPane overlay;

    private final MemberService memberService = new MemberService();
    private List<MemberDTO> allMembers = new ArrayList<>();
    private int currentProjectId;

    @Override
    public void initData(Integer projectId){
        this.currentProjectId = projectId;
        loadData();
    }

    @FXML
    public void initialize() {
        // Lắng nghe ô search để lọc danh sách ngay lập tức
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            List<MemberDTO> filtered = memberService.filterMembers(allMembers, newVal);
            renderMembers(filtered);
        });
    }

    private void loadData() {
        // SỬA LỖI Ở ĐÂY: Thêm 'new' và '.start()' [cite: 54, 82]
        new Thread(() -> {
            try {
                // Gọi Cloud DB lấy danh sách [cite: 206, 262]
                allMembers = memberService.getProjectMembers(currentProjectId);

                // Cập nhật UI phải nằm trong Platform.runLater [cite: 82, 270, 477]
                Platform.runLater(() -> {
                    renderMembers(allMembers);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void renderMembers(List<MemberDTO> members) {
        System.out.println("Render: " + members.size());
        memberContainer.getChildren().clear();
        for (MemberDTO m : members) {
            Node node = createMemberCard(m);
            if (node != null) {
                memberContainer.getChildren().add(node);
            }
        }
    }

    private Node createMemberCard(MemberDTO member) {
        try {
            // Tải file card nhỏ đã chốt fxml [cite: 72, 183]
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/invite/MemberRow.fxml"));
            Node node = loader.load();

            MemberRowController cardController = loader.getController();
            cardController.setData(member);

            return node;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @FXML
    private void handleClose(javafx.event.ActionEvent event) {
        DialogManager.getInstance().close(overlay);
    }
    @FXML
    private void handleOpenInvite(javafx.event.ActionEvent event) {
        DialogManager.getInstance().show(Screen.INVITE_MEMBER);
    }
}