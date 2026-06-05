package com.example.demo.controller;

import com.example.demo.entity.Address;
import com.example.demo.entity.User;
import com.example.demo.service.AddressService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserService userService;

    private User getLoginUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return userService.findById(userId);
    }

    // 地址列表页面
    @GetMapping
    public String list(HttpSession session, Model model) {
        User user = getLoginUser(session);
        model.addAttribute("addresses", addressService.getUserAddresses(user));
        return "addressList";
    }

    // 显示新增地址表单
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("address", new Address());
        return "addressForm";
    }

    // 显示编辑地址表单
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, HttpSession session, Model model) {
        User user = getLoginUser(session);
        Address address = addressService.getAddressById(id);
        if (address == null || !address.getUser().getId().equals(user.getId())) {
            return "redirect:/address";
        }
        model.addAttribute("address", address);
        return "addressForm";
    }

    // 保存新增或修改的地址
    @PostMapping("/save")
    public String saveAddress(@ModelAttribute Address address, HttpSession session) {
        User user = getLoginUser(session);
        address.setUser(user);
        if (address.getId() == null) {
            addressService.addAddress(address);
        } else {
            // 确保编辑的地址属于当前用户
            Address existing = addressService.getAddressById(address.getId());
            if (existing != null && existing.getUser().getId().equals(user.getId())) {
                address.setUser(user); // 保留原关联
                addressService.updateAddress(address);
            } else {
                return "redirect:/address";
            }
        }
        return "redirect:/address";
    }

    // 删除地址
    @GetMapping("/delete/{id}")
    public String deleteAddress(@PathVariable Long id, HttpSession session) {
        User user = getLoginUser(session);
        Address address = addressService.getAddressById(id);
        if (address != null && address.getUser().getId().equals(user.getId())) {
            addressService.deleteAddress(id);
        }
        return "redirect:/address";
    }

    // 设为默认
    @GetMapping("/setDefault/{id}")
    public String setDefault(@PathVariable Long id, HttpSession session) {
        User user = getLoginUser(session);
        addressService.setDefaultAddress(id, user);
        return "redirect:/address";
    }
}