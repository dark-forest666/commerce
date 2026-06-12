package com.example.demo.admin.controller;
import com.example.demo.admin.service.*;
import com.example.demo.entity.Category;
import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.BCryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // 【必须添加】缺失的导入
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminUserService adminUserService;
    @Autowired
    private AdminProductService adminProductService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AdminOrderService adminOrderService;
    @Autowired
    private OperationLogService operationLogService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String loginPage() {
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "用户名和密码不能为空");
            return "login";
        }
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            model.addAttribute("error", "用户不存在");
            return "login";
        }
        if (!BCryptUtil.matches(password, user.getPassword())) {
            model.addAttribute("error", "密码错误");
            return "login";
        }
        if (!"admin".equals(user.getRole())) {
            model.addAttribute("error", "无管理员权限");
            return "login";
        }
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());
        return "redirect:/admin/index";
    }

    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("totalNormalUsers", adminOrderService.getTotalNormalUsers());
        model.addAttribute("totalAdminUsers", adminOrderService.getTotalAdminUsers());
        model.addAttribute("totalProducts", adminOrderService.getTotalProducts());
        model.addAttribute("totalOrders", adminOrderService.getTotalOrders());
        model.addAttribute("totalSales", adminOrderService.getTotalSales());
        model.addAttribute("todayNewUsers", adminOrderService.getTodayNewUsers());
        model.addAttribute("todayOrders", adminOrderService.getTodayOrders());
        model.addAttribute("todaySales", adminOrderService.getTodaySales());
        model.addAttribute("pendingShipOrders", adminOrderService.getPendingShipOrders());
        model.addAttribute("lowStockProducts", adminProductService.getLowStockProducts());
        return "admin/index";
    }

    // -------------------------- 用户管理 --------------------------
    @GetMapping("/users")
    public String userList(Model model) {
        model.addAttribute("users", adminUserService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/toggleStatus")
    public String toggleUserStatus(@RequestParam Long userId, HttpSession session, HttpServletRequest request) {
        adminUserService.toggleUserStatus(userId);
        operationLogService.logOperation((Long) session.getAttribute("userId"), "修改用户状态：" + userId, request);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/resetPassword")
    public String resetPassword(@RequestParam Long userId, HttpSession session, HttpServletRequest request) {
        adminUserService.resetPassword(userId);
        operationLogService.logOperation((Long) session.getAttribute("userId"), "重置用户密码：" + userId, request);
        return "redirect:/admin/users";
    }

    // -------------------------- 商品管理 --------------------------
    @GetMapping("/products")
    public String productList(Model model) {
        model.addAttribute("products", adminProductService.getAllProducts());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/adminProducts";
    }

    @GetMapping("/products/add")
    public String addProductPage(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/productForm";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductPage(@PathVariable Long id, Model model) {
        model.addAttribute("product", adminProductService.getProductById(id));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/productForm";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product, HttpSession session, HttpServletRequest request) {
        adminProductService.saveProduct(product);
        operationLogService.logOperation((Long) session.getAttribute("userId"), "保存商品：" + product.getName(), request);
        return "redirect:/admin/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        adminProductService.deleteProduct(id);
        operationLogService.logOperation((Long) session.getAttribute("userId"), "删除商品：" + id, request);
        return "redirect:/admin/products";
    }

    @GetMapping("/products/toggleStatus/{id}")
    public String toggleProductStatus(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        adminProductService.toggleProductStatus(id);
        operationLogService.logOperation((Long) session.getAttribute("userId"), "切换商品状态：" + id, request);
        return "redirect:/admin/products";
    }

    @PostMapping("/products/batchToggle")
    public String batchToggleStatus(@RequestParam List<Long> ids, @RequestParam Integer status, HttpSession session, HttpServletRequest request) {
        adminProductService.batchToggleStatus(ids, status);
        operationLogService.logOperation((Long) session.getAttribute("userId"), "批量切换商品状态", request);
        return "redirect:/admin/products";
    }

    @PostMapping("/products/batchDelete")
    public String batchDeleteProducts(@RequestParam List<Long> ids, HttpSession session, HttpServletRequest request) {
        adminProductService.batchDelete(ids);
        operationLogService.logOperation((Long) session.getAttribute("userId"), "批量删除商品", request);
        return "redirect:/admin/products";
    }

    // -------------------------- 分类管理 --------------------------
    @GetMapping("/categories")
    public String categoryList(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/categories";
    }

    @PostMapping("/categories/save")
    public String saveCategory(@ModelAttribute Category category, HttpSession session, HttpServletRequest request) {
        categoryService.saveCategory(category);
        operationLogService.logOperation((Long) session.getAttribute("userId"), "保存分类：" + category.getName(), request);
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        categoryService.deleteCategory(id);
        operationLogService.logOperation((Long) session.getAttribute("userId"), "删除分类：" + id, request);
        return "redirect:/admin/categories";
    }

    // -------------------------- 订单管理 --------------------------
    @GetMapping("/orders")
    public String orderList(Model model,
                           @RequestParam(required = false) String keyword,
                           @RequestParam(required = false) Integer status) {
        List<Order> orders;
        if (keyword != null && !keyword.isEmpty()) {
            orders = adminOrderService.searchOrders(keyword);
        } else if (status != null) {
            orders = adminOrderService.getOrdersByStatus(status);
        } else {
            orders = adminOrderService.getAllOrders();
        }
        model.addAttribute("orders", orders);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        return "admin/adminOrders";
    }

    // 【修复】添加RedirectAttributes+异常处理
    @GetMapping("/orders/detail/{id}")
    public String orderDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Order order = adminOrderService.getOrderById(id);
        if (order == null) {
            redirectAttributes.addFlashAttribute("error", "订单不存在或已被删除");
            return "redirect:/admin/orders";
        }
        model.addAttribute("order", order);
        return "admin/orderDetail";
    }

    @PostMapping("/orders/ship")
    public String shipOrder(@RequestParam Long orderId,
                           @RequestParam String expressNo,
                           @RequestParam(required = false) String adminRemark,
                           HttpSession session, HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {
        try {
            adminOrderService.shipOrder(orderId, expressNo, adminRemark);
            operationLogService.logOperation((Long) session.getAttribute("userId"), "发货订单：" + orderId, request);
            redirectAttributes.addFlashAttribute("success", "发货成功");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/orders/detail/" + orderId;
    }

    // 【修复】添加RedirectAttributes+空值处理
    @PostMapping("/orders/batchShip")
    public String batchShipOrders(@RequestParam(required = false) List<Long> ids, 
                                  HttpSession session, 
                                  HttpServletRequest request,
                                  RedirectAttributes redirectAttributes) {
        try {
            adminOrderService.batchShip(ids);
            operationLogService.logOperation((Long) session.getAttribute("userId"), "批量发货订单", request);
            redirectAttributes.addFlashAttribute("success", "批量发货成功");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/orders";
    }

    // -------------------------- 密码修改 --------------------------
    @GetMapping("/changePassword")
    public String changePasswordPage() {
        return "admin/changePassword";
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestParam String oldPassword,
                                @RequestParam String newPassword,
                                HttpSession session, Model model,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {
        Long adminId = (Long) session.getAttribute("userId");
        boolean success = adminUserService.changeAdminPassword(adminId, oldPassword, newPassword);
        if (success) {
            operationLogService.logOperation(adminId, "修改管理员密码", request);
            redirectAttributes.addFlashAttribute("success", "密码修改成功");
            return "redirect:/admin/index";
        } else {
            model.addAttribute("error", "原密码错误");
            return "admin/changePassword";
        }
    }

    // -------------------------- 操作日志 --------------------------
    @GetMapping("/logs")
    public String logList(Model model) {
        model.addAttribute("logs", operationLogService.getAllLogs());
        return "admin/logs";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}