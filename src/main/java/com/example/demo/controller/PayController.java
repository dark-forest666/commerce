package com.example.demo.controller;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.internal.util.AlipaySignature;
import com.example.demo.config.AlipayConfig;
import com.example.demo.entity.Order;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private AlipayConfig alipayConfig;

    @Autowired
    private OrderService orderService;   // 注入订单服务

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(PayController.class);

    // 发起支付（网页支付） - 修改为接收订单ID
    @GetMapping("/page")
    public void pagePay(@RequestParam Long orderId, HttpServletResponse httpResponse) throws IOException {

        // 1. 根据订单ID查询订单（需要确保订单属于当前登录用户，这里省略权限校验，可后续添加）
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            httpResponse.setContentType("text/html;charset=utf-8");
            httpResponse.getWriter().write("订单不存在");
            return;
        }
        if (order.getStatus() != Order.STATUS_WAIT_PAY) {
            httpResponse.getWriter().write("订单状态不是待支付，无法发起支付");
            return;
        }

        // 2. 创建AlipayClient客户端
        AlipayClient alipayClient = new DefaultAlipayClient(
                alipayConfig.getGateway(),
                alipayConfig.getAppid(),
                alipayConfig.getPrivateKey(),
                "json",
                "utf-8",
                alipayConfig.getPublicKey(),
                "RSA2");

        // 3. 创建API请求对象
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(alipayConfig.getNotifyUrl());
        request.setReturnUrl(alipayConfig.getReturnUrl());

        // 4. 填充业务参数（使用真实订单数据）
        String bizContent = "{" +
                "    \"out_trade_no\":\"" + order.getOrderNo() + "\"," +
                "    \"product_code\":\"FAST_INSTANT_TRADE_PAY\"," +
                "    \"total_amount\":\"" + order.getTotalAmount() + "\"," +
                "    \"subject\":\"电商系统订单\"," +
                "    \"body\":\"订单号：" + order.getOrderNo() + "\"," +
                "    \"timeout_express\":\"30m\"}";
        request.setBizContent(bizContent);

        // 5. 发送请求，输出表单
        try {
            String form = alipayClient.pageExecute(request).getBody();
            httpResponse.setContentType("text/html;charset=utf-8");
            httpResponse.getWriter().write(form);
            httpResponse.getWriter().flush();
        } catch (Exception e) {
            e.printStackTrace();
            httpResponse.getWriter().write("支付请求失败");
        }
    }

    // 支付宝异步通知处理 (notify_url) - 增加更新订单状态逻辑
    @PostMapping("/notify")
    @ResponseBody
    public String notify(HttpServletRequest request) {
        try {
            // 1. 获取支付宝POST过来的参数
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
                String name = iter.next();
                String[] values = requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                params.put(name, valueStr);
            }

            // 2. 调用SDK验证签名 (RSA2)
            boolean signVerified = AlipaySignature.rsaCheckV1(params,
                    alipayConfig.getPublicKey(),
                    "UTF-8",
                    "RSA2");

            if (signVerified) {
                String tradeStatus = params.get("trade_status");
                if ("TRADE_SUCCESS".equals(tradeStatus)) {
                    String outTradeNo = params.get("out_trade_no");
                    // 根据订单号更新订单状态为已支付
                    Order order = orderService.getOrderByOrderNo(outTradeNo);
                    if (order != null && order.getStatus() == Order.STATUS_WAIT_PAY) {
                        order.setStatus(Order.STATUS_PAID);
                        orderService.updateOrder(order);   // 需要你在OrderService中添加updateOrder方法
                        // 可选：记录支付宝交易号
                        // order.setPaymentNo(params.get("trade_no"));
                    }
                }
                return "success";
            } else {
                return "failure";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "failure";
        }
    }

    // 同步跳转处理 (return_url) - 保持不变
    @GetMapping("/result")
    public String result(HttpServletRequest request, Map<String, Object> map) {
        long start = System.currentTimeMillis();
        logger.info("=== /pay/result 开始处理 ===");

        String outTradeNo = request.getParameter("out_trade_no");
        logger.info("获取到的 out_trade_no: {}", outTradeNo);

        // 注意：你原来的代码这里只是把 outTradeNo 放入了 model，没有查数据库！
        // 这一点是好的，非常轻量。
        map.put("outTradeNo", outTradeNo);

        long end = System.currentTimeMillis();
        logger.info("=== /pay/result 处理完成，耗时: {} ms ===", (end - start));

        return "payResult";
    }
}