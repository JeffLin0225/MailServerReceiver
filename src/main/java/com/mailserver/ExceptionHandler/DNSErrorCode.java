package com.mailserver.ExceptionHandler;

/**
 * 枚舉類：定義與 DNS 相關的錯誤代碼及其對應的描述訊息
 */
public enum DNSErrorCode {

    // 定義枚舉常量
    DNS_NoAny_MX("找不到該域名的任何 MX 紀錄 (請檢查 Email 中的 @DomainName)"),
    DNS_No_Any_IPV4("未找到任何對應的 IPv4 地址"),
    DNS_ERROR_CODE("未定義的錯誤");

    // 錯誤訊息
    private final String message;

    /**
     * 构造函數
     *
     * @param message 錯誤訊息描述
     */
    DNSErrorCode(String message) {
        this.message = message;
    }

    /**
     * 獲取錯誤訊息
     *
     * @return 錯誤訊息描述
     */
    public String getMessage() {
        return message;
    }
}
