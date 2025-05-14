package com.distopy.controller;

import com.distopy.model.Category;
import com.distopy.model.Product;
import com.distopy.model.UserDtls;
import com.distopy.service.CategoryService;
import com.distopy.service.ProductService;
import com.distopy.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

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

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/signin")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/products")
    public String products(Model m, @RequestParam(value = "category", defaultValue = "") String category) {
        List<Category> categories = categoryService.getAllActiveCategory();
        List<Product> products = productService.getAllActiveProducts(category);
        m.addAttribute("categories", categories);
        m.addAttribute("products", products);
        m.addAttribute("paramValue", category.isEmpty() ? "All" : category);
        return "product";
    }

    @GetMapping("/product/{id}")
    public String product(@PathVariable int id, Model m) {
        Product productById = productService.getProductById(id);
        m.addAttribute("product", productById);
        return "view_product";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute UserDtls user,
                           @RequestParam("img") MultipartFile image,
                           HttpSession session) {

        try {
            // Define nome da imagem
            String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
            user.setProfileImage(imageName);

            // Salva usuário no banco
            UserDtls savedUser = userService.saveUser(user);

            if (!ObjectUtils.isEmpty(savedUser)) {
                // Se tiver imagem, salva no sistema de arquivos
                if (!image.isEmpty()) {
                    File staticImgFolder = new ClassPathResource("static/img").getFile();

                    // Garante que a pasta profile_img exista
                    File profileImgDir = new File(staticImgFolder, "profile_img");
                    if (!profileImgDir.exists()) {
                        profileImgDir.mkdirs(); // cria diretórios se não existirem
                    }

                    // Define caminho completo da imagem
                    Path imagePath = Paths.get(profileImgDir.getAbsolutePath(), image.getOriginalFilename());

                    // Copia a imagem para o destino
                    Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
                }

                session.setAttribute("successMsg", "User saved successfully!");
            } else {
                session.setAttribute("errorMsg", "Something went wrong while saving the user!");
            }

        } catch (IOException e) {
            e.printStackTrace();
            session.setAttribute("errorMsg", "Failed to save image: " + e.getMessage());
        }

        return "redirect:/register";
    }
}
