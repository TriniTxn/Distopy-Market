package com.distopy.controller;

import com.distopy.model.Category;
import com.distopy.model.Product;
import com.distopy.model.UserDtls;
import com.distopy.service.CartService;
import com.distopy.service.CategoryService;
import com.distopy.service.ProductService;
import com.distopy.service.UserService;
import com.distopy.util.CommomUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommomUtils commomUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CartService cartService;

    @ModelAttribute
    public void getUserDetails(Principal p, Model m) {

        if (p != null) {
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);

            /* Essa linha de código m.addAttribute serve para você poder utilizar esse parametro no HTML utilizando
             * Ex: [[${user.getId}]] */
            m.addAttribute("user", userDtls);
            Integer countCart = cartService.getCountCart(userDtls.getId());
            m.addAttribute("countCart", countCart);
        } else {
            m.addAttribute("user", null);
        }

        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        m.addAttribute("categories", allActiveCategory);
    }

    @GetMapping("/")
    public String index(Model m) {
        List<Category> allActiveCategories = categoryService.getAllActiveCategory().stream()
                .sorted((c1, c2) -> c2.getId().compareTo(c1.getId()))
                .limit(6).toList();
        List<Product> allActiveProducts = productService.getAllActiveProducts("").stream()
                .sorted((p1, p2) -> p2.getId().compareTo(p1.getId()))
                .limit(8).toList();

        m.addAttribute("category", allActiveCategories);
        m.addAttribute("product", allActiveProducts);
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
    public String products(Model m,
                           @RequestParam(value = "category", defaultValue = "") String category,
                           @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                           @RequestParam(name = "pageSize", defaultValue = "2") Integer pageSize,
                           @RequestParam(defaultValue = "") String ch) {
        List<Category> categories = categoryService.getAllActiveCategory();
        m.addAttribute("paramValue", category.isEmpty() ? "All" : category);
        m.addAttribute("categories", categories);
        // List<Product> products = productService.getAllActiveProducts(category);
        // m.addAttribute("products", products);

        Page<Product> page;

        if (StringUtils.isEmpty(ch)) {
            page = productService.getAllActiveProductPagination(pageNo, pageSize, category);
        } else {
            page = productService.searchActiveProductPagination(pageNo, pageSize, category, ch);
        }

        List<Product> products = page.getContent();
        m.addAttribute("products", products);
        m.addAttribute("productSize", products.size());

        m.addAttribute("pageNo", page.getNumber());
        m.addAttribute("pageSize", page.getSize());
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());

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

            Boolean existsEmail = userService.existsEmail(user.getEmail());

            if (existsEmail) {
                session.setAttribute("errorMsg", "Email already exists!");
                return "redirect:/register";
            } else {
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
            }

        } catch (IOException e) {
            e.printStackTrace();
            session.setAttribute("errorMsg", "Failed to save image: " + e.getMessage());
        }

        return "redirect:/register";
    }

    // Forgot password code

    @GetMapping("/forgot_password")
    public String showForgotPassword() {
        return "forgot_password.html";
    }

    @PostMapping("/forgot_password")
    public String processForgotPassword(@RequestParam String email, HttpSession session, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        UserDtls userByEmail = userService.getUserByEmail(email);

        if (ObjectUtils.isEmpty(userByEmail)) {
            session.setAttribute("errorMsg", "Invalid email address! Please try again!");
            return "redirect:/forgot_password";
        } else {

            String resetToken = UUID.randomUUID().toString();
            userService.updateUserResetToken(email, resetToken);

            /* Generating the URL to redirect the user to reset password
             * Example: https://localhost:8080/reset_password?token=dajdnafFSAKNFNFfajfnF9291*/

            String url = CommomUtils.generateUrl(request) + "/reset_password?token=" + resetToken;
            System.out.println(url);

            Boolean sendMail = commomUtils.sendMail(url, email);

            if (sendMail) {
                session.setAttribute("successMsg", "Password reset link was successfully sent to your email!");
                return "redirect:/signin";
            } else {
                session.setAttribute("errorMsg", "Failed to send password reset link!");
                return "redirect:/forgot_password";
            }
        }
    }

    @GetMapping("/reset_password")
    public String showResetPassword(@RequestParam String token, Model m, HttpSession session) {

        UserDtls userByToken = userService.getUserByResetToken(token);

        if (userByToken == null) {
            m.addAttribute("msg", "Your link is invalid or expired! Please try again!");
            return "message";
        }
        m.addAttribute("token", token);
        return "reset_password.html";
    }

    @PostMapping("/reset_password")
    public String resetPassword(@RequestParam String token, Model m, @RequestParam String password, HttpSession session) {

        UserDtls userByToken = userService.getUserByResetToken(token);

        if (userByToken == null) {
            m.addAttribute("errorMsg", "Your link is invalid or expired! Please try again!");
            return "message";
        } else {
            userByToken.setPassword(passwordEncoder.encode(password));
            userByToken.setResetToken(null);
            userService.updateUser(userByToken);
            session.setAttribute("successMsg", "Password was successfully reset!");
            m.addAttribute("msg", "Password was successfully reset!");
            return "redirect:/signin";
        }
    }

    @GetMapping("/search")
    public String searchProduct(@RequestParam String ch, Model m) {
        List<Product> searchProduct = productService.getSearchedProduct(ch);
        m.addAttribute("products", searchProduct);
        List<Category> categories = categoryService.getAllActiveCategory();
        m.addAttribute("categories", categories);
        return "product";
    }
}
