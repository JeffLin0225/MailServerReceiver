package com.mailserver.Config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 配置 SMTP 的發送和接收端口
 * 此類從應用配置文件中讀取 SMTP 發送端口和接收端口，並將其存儲為靜態變量。
 */
@Component
public class SmtpConfig {

    // 從配置文件中讀取 SMTP 發送端口
    @Value("${spring.smtp.sender.port}")
    private int senderPort;

    // 從配置文件中讀取 SMTP 接收端口
    @Value("${spring.smtp.receiver.port}")
    private int receiverPort;

    // 靜態變量，用於跨類訪問端口號
    private static int staticSenderPort;
    private static int staticReceiverPort;

    /**
     * 初始化靜態變量，將 @Value 注入的變量賦值給靜態變量
     */
    @PostConstruct
    public void init() {
        staticSenderPort = senderPort;
        staticReceiverPort = receiverPort;
    }

    /**
     * 獲取 SMTP 發送端口
     * @return 靜態發送端口
     */
    public static int getSenderPort() {
        return staticSenderPort;
    }

    /**
     * 獲取 SMTP 接收端口
     * @return 靜態接收端口
     */
    public static int getReceiverPort() {
        return staticReceiverPort;
    }
}
