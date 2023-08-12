package com.mt;

import cn.hutool.extra.mail.MailUtil;
import org.springframework.util.DigestUtils;

/**
 * Author: csy100
 * Date: 2023/8/11
 */
public class TestDemo {
    public static void main(String[] args) {
//        EmailTxt();
        if ("123".equals("1234")) {
            System.out.println("234");
        }
        
        System.out.println(DigestUtils.md5DigestAsHex("123456789".getBytes()));
    }
    
    private static void EmailTxt() {
        MailUtil.send("3054757224@qq.com", "文本邮件测试", "IT技术分享社区，一个有态度的互联网分享平台！", false);
    }
}
