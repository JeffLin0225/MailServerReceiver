package com.mailserver.Entity;

/**
 * SMTP 命令參數實體類
 *
 * 描述 SMTP 通訊過程中的常用命令和參數：
 * - HELO       :  向伺服器介紹自己
 * - MAIL FROM  :  發件人的電子郵件地址
 * - RCPT TO    :  收件人的電子郵件地址
 * - DATA       :  告訴伺服器接下來會發送郵件內容
 * - Subject    :  郵件的主題（標題）
 * - Body       :  郵件的內文，即實際的內容
 * - . (點)     :  一個單獨的點來指示內容結束，並發送。
 * - QUIT       :  結束與伺服器的連接
 */
public class SmtpComment {
    private String helo;        // 伺服器介紹信息
    private String mail_from;    // 發件人的電子郵件地址
    private String rcpt_to;      // 收件人的電子郵件地址
    private String subject;     // 郵件主題
    private String body;        // 郵件內容（內文）

    /*================================================================================*/

    public String getHelo() {
        return helo;
    }

    public void setHelo(String helo) {
        this.helo = helo;
    }

    public String getMail_from() {
        return mail_from;
    }

    public void setMail_from(String mail_from) {
        this.mail_from = mail_from;
    }

    public String getRcpt_to() {
        return rcpt_to;
    }

    public void setRcpt_to(String rcpt_to) {
        this.rcpt_to = rcpt_to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
