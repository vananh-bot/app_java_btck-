package Service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class MailService {
    private final String username = "flowtask.app2006@gmail.com";
    private final String password = "ggfr lgrp onuz hesq";
    public void sendEmail(String toEmail, String subject, String currentOtp) {
        String content = "<div style=\"font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; background-color: #ffffff; border: 1px solid #e1e4e8; border-radius: 12px; overflow: hidden;\">" +
                "    " +
                "    <div style=\"background-color: #f8fbff; padding: 30px; text-align: center;\">" +
                "        <div style=\"display: inline-block; align-items: center;\">" +
          //      "            <span style=\"display: inline-block; background-color: #ffffff; border: 2px solid #2196F3; color: #2196F3; padding: 5px 12px; border-radius: 8px; font-size: 24px; font-weight: bold; margin-right: 10px;\">✓</span>" +
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
        new Thread(() -> {
            try {
                Message message = new MimeMessage(getSession());
                message.setFrom(new InternetAddress(username, "FlowTask Support", "UTF-8"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject(subject);
                message.setContent(content, "text/html; charset=utf-8");

                Transport.send(message);
                System.out.println("==> FlowTask: Đã gửi mail thành công tới " + toEmail);
            } catch (Exception e) {
                System.err.println("==> FlowTask LỖI: Không gửi được mail!");
                e.printStackTrace();
            }
        }).start();
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
}