package Controller;

import DTO.MemberDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class MemberRowController {
    @FXML private ImageView imgAvatar;
    @FXML private Label lblName;
    @FXML private Label lblEmail;
    @FXML private Label lblRole;

    public void setData(MemberDTO member) {
        lblName.setText(member.getName());
        lblEmail.setText(member.getEmail());

        // Hiển thị Role và đổi màu tag cho Admin/Owner [cite: 89, 466]
        lblRole.setText(member.getRole().toString());
        if ("OWNER".equals(member.getRole().toString())) {
            lblRole.getStyleClass().add("role-tag-owner");
        } else {
            lblRole.getStyleClass().add("role-tag-member");
        }

        // Logic bo tròn ảnh 45x45 [cite: 212, 475]
        Circle clip = new Circle(22.5, 22.5, 22.5);
        imgAvatar.setClip(clip);

        // Nạp ảnh Pravatar theo ID (id càng cao ảnh càng khác)
        String avatarUrl = "https://i.pravatar.cc/150?u=" + member.getUserId();
        // 'true' giúp load ảnh ở luồng phụ, không làm giật danh sách [cite: 270]
        imgAvatar.setImage(new Image(avatarUrl, true));
    }
}