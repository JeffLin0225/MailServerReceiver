package com.mailserver.Service;

import com.mailserver.ExceptionHandler.DNSErrorCode;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class DNSHendle {

    /**
     * 傳入 Email，解析並提取 @ 後的 Domain，並將其轉換為對應的 IPv4 地址列表
     *
     * @param email 使用者輸入的電子郵件地址
     * @return IPv4 地址列表，如果無法解析則返回 null
     */
    public String[] emailToDomain(String email) {
        // 檢查是否包含 '@' 符號
        int atIndex = email.indexOf('@');
        if (atIndex != -1 && atIndex < email.length() - 1) {
            System.out.println("Domain 是: " + email.substring(atIndex + 1));
            // 提取域名，並轉換為 IPv4 地址
            return domainToIPv4(email.substring(atIndex + 1));
        }
        return null; // 如果格式不正確，返回 null
    }

    /**
     * 解析 Domain，獲取對應的 IPv4 地址列表
     *
     * @param domain 電子郵件的域名
     * @return IPv4 地址列表
     */
    private String[] domainToIPv4(String domain) {
        List<String> ipAddresses = new ArrayList<>();

        try {
            String[] mxRecords = getMXRecords(domain); // 獲取 MX 記錄
            if (mxRecords.length == 0) {
                return null;
            }
            for (String mx : mxRecords) {
                System.out.println("MX Record: " + mx);
                String ip = getIPAddress(mx); // 將 MX 記錄轉換為 IP 地址
                if (ip != null) {
                    ipAddresses.add(ip);
                    System.out.println("IP Address: " + ip);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ipAddresses.isEmpty()) {
            System.err.println(DNSErrorCode.DNS_No_Any_IPV4.getMessage());
            return null;
        }
        return ipAddresses.toArray(new String[0]);
    }

    /**
     * 獲取指定域名的 MX 記錄
     *
     * @param domain 電子郵件的域名
     * @return MX 記錄數組
     * @throws Exception 如果查詢失敗，則拋出例外
     */
    private static String[] getMXRecords(String domain) throws Exception {
        try {
            Properties props = new Properties();
            props.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");

            DirContext ctx = new InitialDirContext(props);
            Attributes attrs = ctx.getAttributes(domain, new String[] { "MX" });
            Attribute mxAttribute = attrs.get("MX");

            if (mxAttribute == null) {
                System.err.println(DNSErrorCode.DNS_NoAny_MX.getMessage());
                return new String[0];
            }

            String[] mxRecords = new String[mxAttribute.size()];
            for (int i = 0; i < mxAttribute.size(); i++) {
                // 獲取 MX 主機名（移除優先順序資訊）
                mxRecords[i] = mxAttribute.get(i).toString().split(" ")[1];
                System.out.println("MX 主機: " + mxRecords[i]);
            }

            return mxRecords;
        } catch (NamingException e) {
            System.err.println(DNSErrorCode.DNS_ERROR_CODE.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 根據伺服器主機名獲取 IPv4 地址
     *
     * @param host MX 主機名
     * @return 第一個 IPv4 地址，如果無法解析則返回 null
     * @throws Exception 如果查詢失敗，則拋出例外
     */
    private static String getIPAddress(String host) throws Exception {
        InetAddress[] addresses = InetAddress.getAllByName(host);
        if (addresses.length > 0) {
            return addresses[0].getHostAddress(); // 返回第一個 IPv4 地址
        }
        return null;
    }
}
