package com.distopy.controller;

import com.distopy.model.*;
import com.distopy.service.CartService;
import com.distopy.service.OrderService;
import com.distopy.service.UserService;
import com.distopy.service.impl.CategoryServiceImpl;
import com.distopy.util.OrderStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryServiceImpl categoryService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/")
    public String home() {
        return "user/home";
    }

    @ModelAttribute
    public void getUserDetails(Principal p, Model m) {

        if (p != null) {
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);
            m.addAttribute("user", userDtls);
        }

        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        m.addAttribute("categories", allActiveCategory);
    }

    @GetMapping("/addCart")
    public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {
        Cart saveCart = cartService.saveCart(pid, uid);

        if (ObjectUtils.isEmpty(saveCart)) {
            session.setAttribute("errorMsg", "Something went wrong while adding to cart!");
        } else {
            session.setAttribute("successMsg", "Product added to cart!" );
        }
        return "redirect:/product/" + pid;
    }

    @GetMapping("/cart")
    public String loadCartPage(Principal p, Model m) {
        UserDtls user = getLoggedInUserDetails(p);
        List<Cart> carts = cartService.getCartsByUserId(user.getId());
        m.addAttribute("carts", carts);
        if (carts.size() > 0) {
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            m.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        return "/user/cart";
    }

    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid) {
        cartService.updateQuantity(sy, cid);
        return "redirect:/user/cart";
    }

    private UserDtls getLoggedInUserDetails(Principal p) {
        String email = p.getName();
        return userService.getUserByEmail(email);
    }

    @GetMapping("/orders")
    public String orderPage(Principal p, Model m) {
        UserDtls user = getLoggedInUserDetails(p);
        List<Cart> carts = cartService.getCartsByUserId(user.getId());
        m.addAttribute("carts", carts);
        if (carts.size() > 0) {
            Double orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            m.addAttribute("orderPrice", orderPrice);
            m.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        return "/user/order";
    }

    @PostMapping("/saveOrder")
    public String saveOrder(@ModelAttribute OrderRequest request, Principal p){
        UserDtls user = getLoggedInUserDetails(p);
        orderService.saveOrder(user.getId(), request);

        return "redirect:/user/success";
    }

    @GetMapping("/success")
    public String loadSuccess() {
        return "user/success";
    }

    @GetMapping("/userOrders")
    public String myOrder(Model m, Principal p) {
        UserDtls loggedInUserDetails = getLoggedInUserDetails(p);
        List<ProductOrder> orders = orderService.getOrdersByUserId(loggedInUserDetails.getId());
        m.addAttribute("orders", orders);
        return "/user/my_orders";
    }

    @GetMapping("/update_status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {
        OrderStatus[] values = OrderStatus.values();
        String status = null;

        for (OrderStatus orderSt : values) {
            if (orderSt.getId().equals(st)) {
                status = orderSt.getName();
            }
        }

        Boolean updateOrder = orderService.updateOrderStatus(id, status);

        if (updateOrder) {
            session.setAttribute("successMsg", "Order status updated successfully!");
        } else {
            session.setAttribute("errorMsg", "Something went wrong while updating order status!");
        }

        return "redirect:/user/userOrders";
    }
}
