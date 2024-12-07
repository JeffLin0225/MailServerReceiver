package com.mailserver.Service;

import com.mailserver.Config.SmtpConfig;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@Service
public class MailReceiver {

    private static int totalReceivedEmails = 0; // 記錄總共收到的信件數量
    private static int totalFailedEmails = 0; // 記錄失敗的信件數量

    public static void startServer() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(SmtpConfig.getReceiverPort())) {
            System.out.println("SMTP server started on port " + SmtpConfig.getReceiverPort());

            while (true) {
                // 接受客戶端連接
                Socket clientSocket = serverSocket.accept();

                // 使用新執行緒處理每個客戶端
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error starting the server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            out.println("220 localhost SMTP Server Ready");
            String from = null;
            String to = null;
            String cc = null;
            String subject = null;
            StringBuilder messageBody = new StringBuilder();
            String line;
            boolean dataSection = false;

            while ((line = in.readLine()) != null) {
                System.out.println("Received: " + line);

                if (line.startsWith("HELO") || line.startsWith("EHLO")) {
                    out.println("250 Hello");
                } else if (line.startsWith("MAIL FROM:")) {
                    from = line.substring(10).trim();
                    out.println("250 OK");
                } else if (line.startsWith("RCPT TO:")) {
                    to = line.substring(8).trim();
                    out.println("250 OK");
                } else if (line.startsWith("DATA")) {
                    out.println("354 Start mail input");
                    dataSection = true;
                } else if (dataSection) {
                    if (line.equals(".")) {
                        out.println("250 OK: Message accepted");
                        // 獲取郵件接收完成時間
                        Date receiveTime = new Date();
                        String receiveTimeString = String.format("%tF %<tT", receiveTime);
                        boolean savedSuccessfully = saveEmail(from, to,cc, subject, messageBody.toString(), receiveTimeString);

                        synchronized (MailReceiver.class) { // 確保統計是線程安全的
                            if (savedSuccessfully) {
                                totalReceivedEmails++;
                            } else {
                                totalFailedEmails++;
                            }

                            System.out.println("Total emails received successfully: " + totalReceivedEmails);
                            System.out.println("Total failed emails: " + totalFailedEmails);
                        }
                        System.out.println("Email saved locally at " + receiveTimeString);
                        dataSection = false;
                    } else if (line.startsWith("Subject:")) {
                        subject = line.substring(9).trim();
                        messageBody.append(line).append("\n");
                    } else {
                        messageBody.append(line).append("\n");
                    }
                } else if (line.equals("QUIT")) {
                    out.println("221 Bye!");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean saveEmail(String from, String to,String cc ,String subject, String body, String completionTime) {
        String timestamp = new Date().toString().replace(":", "_").replace(" ", "_");
        String filename = "mail_" + timestamp + "_" + subject + ".eml";
        String fileContent =
//        "From: " + from + "\n" +
//                "To: " + to + "\n" +
//"在哪裡?"+
//                "Subject: " + subject + "\n" +
//                "Received at: " + completionTime + "\n\n" +
                body;

        String directoryPath = "C:\\Users\\jaxian\\Desktop"; // 指定儲存路徑
        Path filePath = Paths.get(directoryPath, filename);

        try {
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, fileContent.getBytes());
            System.out.println("Email saved to file: " + filePath.toString());
            return true; // 儲存成功
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error saving email: " + e.getMessage());
            return false; // 儲存失敗
        }
    }
}
