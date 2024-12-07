package com.mailserver;

import com.mailserver.Service.MailReceiver;
import com.mailserver.Service.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class MailserverApplication {


	public static void main(String[] args) {
		SpringApplication.run(MailserverApplication.class, args);
		printLocalIPAddress();

        try {
            MailReceiver.startServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

	private static void printLocalIPAddress() {
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			System.out.println("Local IP Address: " + localHost.getHostAddress());
		} catch (UnknownHostException e) {
			System.err.println("Unable to determine the local IP address.");
			e.printStackTrace();
		}
	}
}
