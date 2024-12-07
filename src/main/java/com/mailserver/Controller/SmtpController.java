package com.mailserver.Controller;

import com.mailserver.Entity.SmtpComment;
import com.mailserver.Service.DNSHendle;
import com.mailserver.Service.MailSender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SmtpController {

    @Autowired
    private MailSender mailSender;

    @Autowired
    private DNSHendle dnsHendle;

    @PostMapping("/sendMail")
    public String sendMail(@RequestBody SmtpComment smtpComment) {
        dnsHendle.emailToDomain(smtpComment.getMail_from());  // 查找 domain -> ip

        StringBuilder str = new StringBuilder();
        str.append("印出打進去的參數: " + smtpComment);
        return str.toString();
    }
}
