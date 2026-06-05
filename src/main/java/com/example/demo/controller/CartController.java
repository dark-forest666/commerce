package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.CartService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    private User getLoginUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return userService.findById(userId);
    }

    // 查看购物车
    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        User user = getLoginUser(session);
        model.addAttribute("cartItems", cartService.getCartItems(user));
        model.addAttribute("totalPrice", cartService.getSelectedTotalPrice(user));
        return "cart";
    }

    // 加入购物车，重定向回来源页面
    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, @RequestParam int quantity,
                            HttpSession session, HttpServletRequest request) {
        User user = getLoginUser(session);
        cartService.addToCart(user, productId, quantity);
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/products");
    }

    // 更新数量（同步方式，保留原有接口）
    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long cartItemId, @RequestParam int quantity) {
        cartService.updateQuantity(cartItemId, quantity);
        return "redirect:/cart";
    }

    // AJAX 更新数量
    @PostMapping("/updateAjax")
    @ResponseBody
    public String updateQuantityAjax(@RequestParam Long cartItemId, @RequestParam int quantity) {
        cartService.updateQuantity(cartItemId, quantity);
        return "ok";
    }

    // 删除（同步）
    @PostMapping("/remove")
    public String remove(@RequestParam Long cartItemId) {
        cartService.removeCartItem(cartItemId);
        return "redirect:/cart";
    }

    // AJAX 删除
    @PostMapping("/removeAjax")
    @ResponseBody
    public String removeAjax(@RequestParam Long cartItemId) {
        cartService.removeCartItem(cartItemId);
        return "ok";
    }

    // 切换选中（同步）
    @PostMapping("/toggleSelect")
    public String toggleSelect(@RequestParam Long cartItemId, @RequestParam Boolean selected) {
        cartService.toggleSelect(cartItemId, selected);
        return "redirect:/cart";
    }

    // AJAX 切换选中
    @PostMapping("/toggleSelectAjax")
    @ResponseBody
    public String toggleSelectAjax(@RequestParam Long cartItemId, @RequestParam Boolean selected) {
        cartService.toggleSelect(cartItemId, selected);
        return "ok";
    }
}