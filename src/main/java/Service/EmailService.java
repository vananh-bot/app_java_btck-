package Service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailService {

    private final String fromEmail = "flowtask.app2006@gmail.com";
    private final String password = "ggfr lgrp onuz hesq";

    public void sendInvite(String toEmail, String inviteeName, String projectName, String token) {

        // Cấu hình kết nối Gmail
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(fromEmail, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail)
            );

            // Cập nhật tiêu đề email động theo tên dự án
            message.setSubject("Lời mời tham gia dự án: " + projectName);

            // Sử dụng Text Block (""") để chứa nguyên bản HTML thiết kế mới
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

            // Thiết lập nội dung là HTML
            message.setContent(content, "text/html; charset=UTF-8");

            Transport.send(message);

            System.out.println("Đã gửi email mời thành công đến: " + toEmail);

        } catch (Exception e) {
            System.out.println("Lỗi khi gửi email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}