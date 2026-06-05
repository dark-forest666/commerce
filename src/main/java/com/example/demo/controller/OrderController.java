package com.example.demo.controller;

import com.example.demo.entity.Address;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import com.example.demo.service.AddressService;
import com.example.demo.service.CartService;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserService userService;

    private User getLoginUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return userService.findById(userId);
    }

    // 订单确认页（从购物车点击去结算进入）
    @GetMapping("/checkout")
    public String checkoutPage(HttpSession session, Model model) {
        User user = getLoginUser(session);
        List<Address> addresses = addressService.getUserAddresses(user);
        if (addresses.isEmpty()) {
            // 没有地址则提示先去添加地址
            return "redirect:/address?needAddress=true";
        }
        List<CartItem> selectedItems = cartService.getSelectedItems(user);
        if (selectedItems.isEmpty()) {
            return "redirect:/cart"; // 没有选中商品，回购物车
        }
        double totalPrice = cartService.getSelectedTotalPrice(user);
        model.addAttribute("addresses", addresses);
        model.addAttribute("selectedItems", selectedItems);
        model.addAttribute("totalPrice", totalPrice);
        return "orderConfirm";
    }

    // 提交订单
    @PostMapping("/create")
    public String createOrder(@RequestParam Long addressId, HttpSession session) {
        User user = getLoginUser(session);
        Address address = addressService.getAddressById(addressId);
        if (address == null || !address.getUser().getId().equals(user.getId())) {
            return "redirect:/order/checkout";
        }
        List<CartItem> selectedItems = cartService.getSelectedItems(user);
        if (selectedItems.isEmpty()) {
            return "redirect:/cart";
        }
        Order order = orderService.createOrder(user, address, selectedItems);
        return "redirect:/order/success?orderId=" + order.getId();
    }

    // 下单成功页（可展示订单信息，并引导去支付）
    @GetMapping("/success")
    public String success(@RequestParam Long orderId, HttpSession session, Model model) {
        User user = getLoginUser(session);
        Order order = orderService.getOrderByIdAndUser(orderId, user);
        if (order == null) {
            return "redirect:/myOrders";
        }
        model.addAttribute("order", order);
        return "pay"; // 复用支付页面，后续可替换为支付宝
    }

    // 我的订单列表
    @GetMapping("/myOrders")
    public String myOrders(HttpSession session, Model model) {
        User user = getLoginUser(session);
        List<Order> orders = orderService.getOrdersByUser(user);
        model.addAttribute("orders", orders);
        return "myOrders";
    }

    // 订单详情
    @GetMapping("/detail/{orderId}")
    public String orderDetail(@PathVariable Long orderId, HttpSession session, Model model) {
        User user = getLoginUser(session);
        Order order = orderService.getOrderByIdAndUser(orderId, user);
        if (order == null) {
            return "redirect:/order/myOrders";
        }
        model.addAttribute("order", order);
        return "orderDetail";
    }

    // 取消订单（仅待支付）
    @PostMapping("/cancel")
    public String cancelOrder(@RequestParam Long orderId, HttpSession session) {
        User user = getLoginUser(session);
        orderService.cancelOrder(orderId, user);
        return "redirect:/order/detail/" + orderId;
    }

    // 模拟支付
    @PostMapping("/simulatePay")
    public String simulatePay(@RequestParam Long orderId, HttpSession session) {
        User user = getLoginUser(session);
        orderService.simulatePay(orderId, user);
        return "redirect:/order/detail/" + orderId;
    }
}