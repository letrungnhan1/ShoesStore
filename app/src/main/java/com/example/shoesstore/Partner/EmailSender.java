package com.example.shoesstore.Partner;

import com.example.shoesstore.utilities.Constants;

import java.util.Properties;
import javax.mail.Session;
import javax.mail.Authenticator;
import javax.mail.internet.MimeMessage;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;

public class EmailSender {

    private String senderEmail;
    private String senderPassword;

    public EmailSender(String senderEmail, String senderPassword) {
        this.senderEmail = senderEmail;
        this.senderPassword = senderPassword;
    }

    public void sendEmail(Integer otp, String receiverEmail) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", Constants.Gmail_Host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        MimeMessage message = new MimeMessage(session);
        try {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));
            message.setSubject("Xác thực tài khoản với mã OTP");

            // Định dạng nội dung email
            String emailContent = "Chào bạn,\n\n"
                    + "Bạn đã yêu cầu xác thực tài khoản của mình. Đây là mã OTP của bạn:\n\n"
                    + "Mã OTP: " + otp + "\n\n"
                    + "Vui lòng nhập mã này vào trang web hoặc ứng dụng của chúng tôi để hoàn tất quy trình xác thực.\n\n"
                    + "Lưu ý: Mã OTP này chỉ có hiệu lực trong vòng 1 phút kể từ khi bạn nhận được email này.\n\n"
                    + "Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này. Nếu bạn vẫn gặp vấn đề hoặc cần sự trợ giúp, đừng ngần ngại liên hệ với chúng tôi.\n\n"
                    + "Trân trọng,\n"
                    + "Chat App Huflit";

            // Thiết lập nội dung của email
            message.setText(emailContent);

            Thread thread = new Thread(() -> {
                try {
                    Transport.send(message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
