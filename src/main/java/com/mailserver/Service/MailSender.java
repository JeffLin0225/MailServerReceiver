package com.mailserver.Service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import com.sun.mail.smtp.SMTPTransport;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class MailSender {

    private final ExecutorService executorService = Executors.newFixedThreadPool(10); // 根據需求調整執行緒數量
    private int successCount = 0;  // 成功發送郵件的計數
    private int failureCount = 0;  // 失敗發送郵件的計數

    // 非同步發送郵件
    public void sendEmailAsync(String from, String recipient, String cc, String subject, String messageBody) {
        executorService.submit(() -> sendEmail(from, recipient, cc, subject, messageBody));
    }

    // 同步發送郵件的具體實現
    private void sendEmail(String from, String recipient, String cc, String subject, String messageBody) {
        // 設置郵件屬性
        Properties props = new Properties();
        props.put("mail.smtp.host", "localhost");
        props.put("mail.smtp.port", "2526");
        props.put("mail.smtp.auth", "true"); // 啟用身份驗證

        // 創建郵件會話
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("jeff@jxdns.dnsking.com", "123456");
            }
        });

        try {
            // 創建 MIMEMessage
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));

            // 設置 CC 收件者
            if (cc != null && !cc.isEmpty()) {
                message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
            }

            message.setSubject(subject, "UTF-8");
            message.setText(messageBody, "UTF-8");

            // 使用 SMTPTransport 發送郵件
            SMTPTransport smtpTransport = (SMTPTransport) session.getTransport("smtp");
            smtpTransport.connect("localhsot", "jeff@jxdns.dnsking.com", "123456");

            smtpTransport.sendMessage(message, message.getAllRecipients());
            synchronized (this) { // 確保計數器是線程安全的
                successCount++;
                System.out.println("Email sent successfully to " + recipient);
            }

            smtpTransport.close(); // 記得在發送後關閉傳輸
        } catch (MessagingException e) {
            synchronized (this) { // 確保計數器是線程安全的
                failureCount++;
                System.err.println("Failed to send email to " + recipient + ": " + e.getMessage());
            }
        }
    }

    // 關閉資源
    public void close() {
        executorService.shutdown(); // 關閉執行緒池
        System.out.println("Total emails sent successfully: " + successCount);
        System.out.println("Total emails failed to send: " + failureCount);
    }
}
