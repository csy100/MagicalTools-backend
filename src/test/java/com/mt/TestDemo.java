package com.mt;

import cn.hutool.extra.mail.MailUtil;

/**
 * Author: csy100
 * Date: 2023/8/11
 */
public class TestDemo {
    public static void main(String[] args) {
        EmailTxt();
    }
    
    private static void EmailTxt() {
        MailUtil.send("3054757224@qq.com", "文本邮件测试", "IT技术分享社区，一个有态度的互联网分享平台！", false);
    }
}
