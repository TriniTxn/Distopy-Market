package com.distopy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/")
    public String index(){
        return "admin/index";
    }

    @GetMapping("/addProduct")
    public String addProduct(){
        return "admin/add_product";
    }

    @GetMapping("/category")
    public String category(){
        return "admin/category";
    }
}
