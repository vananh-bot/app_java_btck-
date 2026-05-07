package Service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class MailService {
    private final String username = "flowtask.app2006@gmail.com";
    private final String password = "cxvo hbaa tidf ofwd";
    public void sendEmail(String toEmail, String subject, String htmlContent) {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                Message message = new MimeMessage(getSession());
                message.setFrom(new InternetAddress(username, "FlowTask Support", "UTF-8"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject(subject);
                message.setContent(htmlContent, "text/html; charset=utf-8");

                Transport.send(message);
                System.out.println("==> FlowTask: Đã gửi mail thành công!");
            } catch (Exception e) {
                System.err.println("==> FlowTask LỖI: Không gửi được mail!");
                e.printStackTrace();
            }
        }).start();
    }
    public void sendOtpEmail(String toEmail, String currentOtp) {
        String content = "<div style=\"font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; background-color: #ffffff; border: 1px solid #e1e4e8; border-radius: 12px; overflow: hidden;\">" +
                "    " +
                "    <div style=\"background-color: #f8fbff; padding: 30px; text-align: center;\">" +
                "        <div style=\"display: inline-block; align-items: center;\">" +
                "            <span style=\"font-size: 32px; font-weight: 800; color: #2196F3; vertical-align: middle;\">FlowTask</span>" +
                "        </div>" +
                "    </div>" +
                "    " +
                "    " +
                "    <div style=\"padding: 40px; text-align: center;\">" +
                "        <h2 style=\"color: #333; font-size: 22px; font-weight: 700; margin-bottom: 20px;\">Chào mừng bạn đã trở thành một thành viên của <strong>FlowTask</strong>!</h2>" +
                "        <p style=\"color: #666; font-size: 16px; line-height: 1.6; margin-bottom: 30px;\">" +
                "            Chúng tôi rất vui khi bạn đã lựa chọn đồng hành cùng <strong>FlowTask</strong> để tối ưu hóa công việc. " +
                "            Để xác minh tài khoản, vui lòng sử dụng mã xác thực dưới đây:" +
                "        </p>" +
                "        " +
                "        " +
                "        <div style=\"background-color: #1E88E5; padding: 25px; border-radius: 8px; display: inline-block; min-width: 280px; margin-bottom: 25px;\">" +
                "            <span style=\"color: #ffffff; font-size: 42px; font-weight: bold; letter-spacing: 12px; font-family: monospace;\">" + currentOtp + "</span>" +
                "        </div>" +
                "        " +
                "        " +
                "        <p style=\"color: #FF9800; font-size: 14px; font-weight: 600; margin-bottom: 35px;\">" +
                "            ⚠️ Mã xác thực này sẽ hết hạn trong vòng 03:00 phút. Vui lòng không chia sẻ mã này với bất kỳ ai." +
                "        </p>" +
                "        " +
                "        " +
                "        <div style=\"margin-top: 20px;\">" +
                "            <a href=\"#\" style=\"background-color: #2196F3; color: #ffffff; padding: 15px 40px; text-decoration: none; font-weight: bold; border-radius: 6px; font-size: 16px; text-transform: uppercase;\">VÀO FLOWTASK NGAY</a>" +
                "        </div>" +
                "    </div>" +
                "    " +
                "    " +
                "    <div style=\"padding: 25px; text-align: center; color: #888; font-size: 12px; border-top: 1px solid #f0f0f0;\">" +
                "        <p style=\"margin: 5px 0;\">Trân trọng,</p>" +
                "        <p style=\"margin: 5px 0; font-weight: bold; color: #555;\">Đội ngũ phát triển FlowTask - ProPTIT</p>" +
                "        <div style=\"margin-top: 15px;\">" +
                "            <a href=\"#\" style=\"color: #2196F3; text-decoration: none;\">Hỗ trợ</a> | <a href=\"#\" style=\"color: #2196F3; text-decoration: none;\">Tài khoản</a>" +
                "        </div>" +
                "    </div>" +
                "</div>";
        sendEmail(toEmail, "Xác thực tài khoản FlowTask", content);
    }
    public void resendOtpEmail(String toEmail, String currentOtp) {
        String content = "<div style=\"font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; background-color: #ffffff; border: 1px solid #e1e4e8; border-radius: 12px; overflow: hidden;\">" +
                "    " +
                "    <div style=\"background-color: #f8fbff; padding: 30px; text-align: center;\">" +
                "        <div style=\"display: inline-block; align-items: center;\">" +
                "            <span style=\"font-size: 32px; font-weight: 800; color: #2196F3; vertical-align: middle;\">FlowTask</span>" +
                "        </div>" +
                "    </div>" +
                "    " +
                "    " +
                "    <div style=\"padding: 40px; text-align: center;\">" +
                "        <h2 style=\"color: #333; font-size: 22px; font-weight: 700; margin-bottom: 20px;\">Chào mừng bạn đã trở thành một thành viên của <strong>FlowTask</strong>!</h2>" +
                "        <p style=\"color: #666; font-size: 16px; line-height: 1.6; margin-bottom: 30px;\">" +
                "            Chúng tôi rất vui khi bạn đã lựa chọn đồng hành cùng <strong>FlowTask</strong> để tối ưu hóa công việc. " +
                "            Để xác minh tài khoản, vui lòng sử dụng mã xác thực dưới đây:" +
                "        </p>" +
                "        " +
                "        " +
                "        <div style=\"background-color: #1E88E5; padding: 25px; border-radius: 8px; display: inline-block; min-width: 280px; margin-bottom: 25px;\">" +
                "            <span style=\"color: #ffffff; font-size: 42px; font-weight: bold; letter-spacing: 12px; font-family: monospace;\">" + currentOtp + "</span>" +
                "        </div>" +
                "        " +
                "        " +
                "        <p style=\"color: #FF9800; font-size: 14px; font-weight: 600; margin-bottom: 35px;\">" +
                "            ⚠️ Mã xác thực này sẽ hết hạn trong vòng 03:00 phút. Vui lòng không chia sẻ mã này với bất kỳ ai." +
                "        </p>" +
                "        " +
                "        " +
                "        <div style=\"margin-top: 20px;\">" +
                "            <a href=\"#\" style=\"background-color: #2196F3; color: #ffffff; padding: 15px 40px; text-decoration: none; font-weight: bold; border-radius: 6px; font-size: 16px; text-transform: uppercase;\">VÀO FLOWTASK NGAY</a>" +
                "        </div>" +
                "    </div>" +
                "    " +
                "    " +
                "    <div style=\"padding: 25px; text-align: center; color: #888; font-size: 12px; border-top: 1px solid #f0f0f0;\">" +
                "        <p style=\"margin: 5px 0;\">Trân trọng,</p>" +
                "        <p style=\"margin: 5px 0; font-weight: bold; color: #555;\">Đội ngũ phát triển FlowTask - ProPTIT</p>" +
                "        <div style=\"margin-top: 15px;\">" +
                "            <a href=\"#\" style=\"color: #2196F3; text-decoration: none;\">Hỗ trợ</a> | <a href=\"#\" style=\"color: #2196F3; text-decoration: none;\">Tài khoản</a>" +
                "        </div>" +
                "    </div>" +
                "</div>";
        sendEmail(toEmail, "Gửi lại mã xác thực tài khoản FlowTask", content);
    }
    public void sendNewTaskAssignment(String toEmail, String projectName, String taskTitle, String assigner, String deadline) {
        String description = "<b>" + assigner + "</b> vừa giao một nhiệm vụ mới <b>" + taskTitle + "</b> trong dự án <b>" + projectName + "</b>.";
        String content = createBaseTemplate(
                "Nhiệm vụ mới được giao!",
                description,
                taskTitle,
                "Hạn chót: " + deadline,
                "#2196F3"
        );
        sendEmail(toEmail, "[FlowTask] Nhiệm vụ mới từ dự án " + projectName, content);
    }
    public void sendTaskReminder(String toEmail, String projectName, String taskTitle, String timeLeft) {
        String description = "Thông báo nhắc nhở! Nhiệm vụ thuộc dự án <b>" + projectName + "</b> của bạn sắp đến hạn chót.";
        String content = createBaseTemplate(
                "Nhắc nhở: 24 tiếng cuối cùng!",
                description,
                taskTitle,
                "Thời gian còn lại: ~" + timeLeft,
                "#FF9800"
        );
        sendEmail(toEmail, "[FlowTask] Nhắc nhở: Task sắp đến hạn (" + projectName + ")", content);
    }
    public void sendTaskOverdue(String toEmail, String projectName, String taskTitle, String overdueTime) {
        String description = "<b>Cảnh báo!</b> Công việc trong dự án <b>" + projectName + "</b> đã quá hạn. Điều này có thể ảnh hưởng đến tiến độ chung của nhóm.";
        String content = createBaseTemplate(
                "Cảnh báo: Đã quá hạn!",
                description,
                taskTitle,
                "Trạng thái: Trễ " + overdueTime,
                "#F44336"
        );
        sendEmail(toEmail, "[FlowTask] CẢNH BÁO QUÁ HẠN: " + taskTitle, content);
    }
    private String createBaseTemplate(String title, String description, String taskName, String footerDetail, String brandColor) {
        String headerIcon = "https://cdn-icons-png.flaticon.com/512/4345/4345573.png"; // Mặc định
        if (brandColor.equalsIgnoreCase("#F44336")) headerIcon = "https://cdn-icons-png.flaticon.com/512/564/564619.png";
        if (brandColor.equalsIgnoreCase("#FF9800")) headerIcon = "https://cdn-icons-png.flaticon.com/512/2972/2972531.png";
        if (brandColor.equalsIgnoreCase("#4CAF50")) headerIcon = "https://cdn-icons-png.flaticon.com/512/1165/1165230.png";
        String bodyEmoji = "📝";
        if (brandColor.equalsIgnoreCase("#F44336")) bodyEmoji = "🚨";
        if (brandColor.equalsIgnoreCase("#FF9800")) bodyEmoji = "⏳";
        if (brandColor.equalsIgnoreCase("#4CAF50")) bodyEmoji = "🎉";
        return "<div style=\"background-color: #f4f7fa; padding: 40px 0; font-family: 'Segoe UI', Roboto, Arial, sans-serif;\">" +
                "    <div style=\"max-width: 580px; margin: 0 auto; background-color: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.05); border: 1px solid #eef2f6;\">" +
                "        " +
                "        <!-- Header -->" +
                "        <div style=\"padding: 30px 40px; border-bottom: 1px solid #f0f0f0;\">" +
                "            <table width=\"100%\">" +
                "                <tr>" +
                "                    <td>" +
                "                        <div style=\"font-size: 24px; font-weight: 800; color: #1a1f36; letter-spacing: -1px;\">Flow<span style=\"color: " + brandColor + ";\">Task</span></div>" +
                "                    </td>" +
                "                    <td align=\"right\">" +
                "                        <img src=\"" + headerIcon + "\" width=\"30\" height=\"30\" style=\"vertical-align: middle; opacity: 0.9;\">" +
                "                    </td>" +
                "                </tr>" +
                "            </table>" +
                "        </div>" +
                "        " +
                "        <div style=\"padding: 40px;\">" +
                "            <div style=\"margin-bottom: 25px;\">" +
                "                <!-- Fix ở đây: Dùng Emoji thay vì chèn biến link vào span -->" +
                "                <div style=\"font-size: 45px; margin-bottom: 10px;\">" + bodyEmoji + "</div>" +
                "                <h1 style=\"color: #111111; font-size: 26px; font-weight: 800; margin: 10px 0 10px 0; line-height: 1.2;\">" + title + "</h1>" +
                "                <p style=\"color: #555555; font-size: 15px; line-height: 1.6; margin: 0;\">" + description + "</p>" +
                "            </div>" +
                "            " +
                "            <div style=\"background-color: #fcfcfc; border-radius: 12px; padding: 25px; border-left: 6px solid " + brandColor + "; border-right: 1px solid #eee; border-top: 1px solid #eee; border-bottom: 1px solid #eee;\">" +
                "                <div style=\"color: #999; font-size: 11px; text-transform: uppercase; font-weight: 800; letter-spacing: 1px; margin-bottom: 8px;\">Nhiệm vụ cần thực hiện</div>" +
                "                <div style=\"color: #222; font-size: 18px; font-weight: 700; margin-bottom: 12px; line-height: 1.4;\">" + taskName + "</div>" +
                "                <div style=\"background-color: " + brandColor + "15; color: " + brandColor + "; padding: 6px 12px; border-radius: 6px; font-size: 13px; font-weight: 700; display: inline-block;\">" +
                "                    " + footerDetail + "" +
                "                </div>" +
                "            </div>" +
                "            " +
                "            <div style=\"text-align: center; margin-top: 35px;\">" +
                "                <a href=\"#\" style=\"background-color: #1a1f36; color: #ffffff; padding: 16px 35px; text-decoration: none; font-weight: 800; border-radius: 12px; font-size: 14px; display: inline-block; letter-spacing: 0.5px;\">MỞ FLOWTASK NGAY</a>" +
                "            </div>" +
                "        </div>" +
                "        " +
                "        <div style=\"background-color: #fafafa; padding: 20px; text-align: center; border-top: 1px solid #eee;\">" +
                "            <p style=\"margin: 0; color: #aaa; font-size: 11px; font-weight: 500;\">Đội ngũ phát triển FlowTask - ProPTIT</p>" +
                "        </div>" +
                "    </div>" +
                "</div>";
    }
    private Session getSession() {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        return Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }
    public void sendJoinSuccessEmail(String toEmail, String projectName, String newMemberName) {
        String headerIcon = "https://cdn-icons-png.flaticon.com/512/4345/4345573.png";
        String content =
                "<div style=\"background-color: #f4f7fa; padding: 40px 0; font-family: 'Segoe UI', Arial, sans-serif;\">" +
                        "    <div style=\"max-width: 580px; margin: 0 auto; background-color: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.05);\">" +
                        "        " +
                        "        <!-- Header -->" +
                        "        <div style=\"padding: 30px 40px; border-bottom: 1px solid #f0f0f0;\">" +
                        "            <div style=\"font-size: 24px; font-weight: 800; color: #1a1f36;\">Flow<span style=\"color: #4CAF50;\">Task</span></div>" +
                        "        </div>" +
                        "        " +
                        "        <!-- Content -->" +
                        "        <div style=\"padding: 40px; text-align: center;\">" +
                        "            <div style=\"font-size: 50px; margin-bottom: 15px;\">🎉</div>" +
                        "            <h1 style=\"color: #111111; font-size: 28px; font-weight: 800; margin-bottom: 15px;\">Chào mừng đồng đội mới!</h1>" +
                        "            " +
                        "            <p style=\"color: #555555; font-size: 16px; line-height: 1.6; margin-bottom: 25px;\">" +
                        "                Tuyệt vời! <b>" + newMemberName + "</b> đã chính thức gia nhập dự án <b>" + projectName + "</b>.<br>" +
                        "                Hãy cùng nhau tạo nên những kết quả xuất sắc nhé!" +
                        "            </p>" +
                        "            " +
                        "            <!-- Nút bấm -->" +
                        "            <div style=\"margin-top: 35px;\">" +
                        "                <a href=\"#\" style=\"background-color: #1a1f36; color: #ffffff; padding: 16px 40px; text-decoration: none; font-weight: 800; border-radius: 12px; font-size: 14px; display: inline-block;\">VÀO DỰ ÁN NGAY</a>" +
                        "            </div>" +
                        "        </div>" +
                        "        " +
                        "        <!-- Footer -->" +
                        "        <div style=\"background-color: #fafafa; padding: 20px; text-align: center; border-top: 1px solid #eee;\">" +
                        "            <p style=\"margin: 0; color: #aaa; font-size: 11px;\">Đội ngũ phát triển FlowTask - ProPTIT</p>" +
                        "        </div>" +
                        "    </div>" +
                        "</div>";

        sendEmail(toEmail, "[FlowTask] Thành viên mới đã gia nhập dự án " + projectName, content);
    }
    public void sendInvite(String toEmail, String inviteeName, String projectName, String token) {
        String content = """
            <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 500px; margin: 0 auto; text-align: center; padding: 50px 20px; background-color: #ffffff; border: 1px solid #eef2f6; border-radius: 12px; box-shadow: 0 4px 10px rgba(0,0,0,0.03);">
                <h1 style="color: #0c2c5c; font-size: 26px; font-weight: 800; margin-bottom: 15px; line-height: 1.3;">
                    Lời mời tham gia dự án<br>
                    <span style="color: #2e59d9;">%s</span>
                </h1>
                <div style="width: 40px; height: 3px; background-color: #2e59d9; margin: 0 auto 25px auto; border-radius: 2px;"></div>
                <p style="color: #4a5b70; font-size: 15px; line-height: 1.6; margin-bottom: 35px; padding: 0 10px;">
                    Chào <strong>%s</strong>! Bạn đã được mời tham gia vào không gian làm việc của chúng tôi để cùng kiến tạo những giá trị mới trong dự án <strong>%s</strong> sắp tới.
                </p>
                <div style="background-color: #f4f7fb; border-radius: 10px; padding: 25px 20px; margin-bottom: 35px;">
                    <p style="color: #6a7b8f; font-size: 11px; font-weight: 700; text-transform: uppercase; letter-spacing: 1.5px; margin: 0 0 12px 0;">
                        Mã tham gia dự án
                    </p>
                    <p style="color: #2e59d9; font-size: 22px; font-weight: 800; letter-spacing: 3px; margin: 0;">
                        %s
                    </p>
                </div>
                <div style="margin-top: 40px; font-size: 13px; color: #a0aec0; background-color: #f8fafc; padding: 15px; border-radius: 8px;">
                    Vui lòng sao chép mã phía trên và dán vào phần mềm FlowTask để bắt đầu làm việc.
                </div>
            </div>
        """.formatted(projectName, inviteeName, projectName, token);
        sendEmail(toEmail, "Lời mời tham gia dự án: " + projectName, content);
    }
    public void sendForgotPasswordToken(String toEmail, String token) {
        String content = """
        <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 500px; margin: 0 auto; text-align: center; padding: 50px 20px; background-color: #ffffff; border: 1px solid #eef2f6; border-radius: 12px; box-shadow: 0 4px 10px rgba(0,0,0,0.03);">
            <h1 style="color: #0c2c5c; font-size: 24px; font-weight: 800; margin-bottom: 15px;">
                Khôi phục mật khẩu
            </h1>
            <div style="width: 40px; height: 3px; background-color: #e53e3e; margin: 0 auto 25px auto; border-radius: 2px;"></div>
            <p style="color: #4a5b70; font-size: 15px; line-height: 1.6; margin-bottom: 30px; padding: 0 10px;">
                Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản <strong>%s</strong>. 
                Vui lòng sử dụng mã xác minh dưới đây để tiếp tục.
            </p>
            <div style="background-color: #fff5f5; border: 1px dashed #feb2b2; border-radius: 10px; padding: 25px 20px; margin-bottom: 30px;">
                <p style="color: #c53030; font-size: 11px; font-weight: 700; text-transform: uppercase; letter-spacing: 1.5px; margin: 0 0 12px 0;">
                    Mã xác minh (OTP)
                </p>
                <p style="color: #c53030; font-size: 32px; font-weight: 800; letter-spacing: 8px; margin: 0;">
                    %s
                </p>
            </div>
            <p style="color: #718096; font-size: 13px; margin-bottom: 25px;">
                Mã này có hiệu lực trong vòng 1 phút. <br>
                Nếu bạn không yêu cầu thay đổi này, hãy bỏ qua email này.
            </p>
            <div style="margin-top: 40px; font-size: 12px; color: #a0aec0; border-top: 1px solid #edf2f7; padding-top: 20px;">
                Đây là email tự động từ hệ thống quản lý công việc <strong>FlowTask</strong>.
            </div>
        </div>
        """.formatted(toEmail, token);
        sendEmail(toEmail, "Mã xác minh khôi phục mật khẩu - FlowTask", content);
    }
}