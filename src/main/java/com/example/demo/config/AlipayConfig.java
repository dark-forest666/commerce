package com.example.demo.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class AlipayConfig {

    @Value("${alipay.appid}")
    private String appid;

    @Value("${alipay.privateKey}")
    private String privateKey;

    @Value("${alipay.publicKey}")
    private String publicKey;

    @Value("${alipay.gateway}")
    private String gateway;

    @Value("${alipay.notifyUrl}")
    private String notifyUrl;

    @Value("${alipay.returnUrl}")
    private String returnUrl;
}