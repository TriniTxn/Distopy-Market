package com.distopy.controller;

import com.distopy.model.Category;
import com.distopy.model.Product;
import com.distopy.model.ProductOrder;
import com.distopy.model.UserDtls;
import com.distopy.service.CategoryService;
import com.distopy.service.OrderService;
import com.distopy.service.ProductService;
import com.distopy.service.UserService;
import com.distopy.util.CommomUtils;
import com.distopy.util.OrderStatus;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
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


@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CommomUtils commomUtils;

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
    public String index(){
        return "admin/index";
    }

    @GetMapping("/loadAddProduct")
    public String loadAddProduct(Model m){
        List<Category> categories = categoryService.getAllCategory();
        m.addAttribute("categories", categories);
        return "admin/add_product";
    }

    @GetMapping("/category")
    public String category(Model m,
                           @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                           @RequestParam(name = "pageSize", defaultValue = "4") Integer pageSize){
        Page<Category> categoryPagination = categoryService.getAllCategoryPagination(pageNo, pageSize);
        List<Category> categories = categoryPagination.getContent();
        m.addAttribute("categories", categories);
        m.addAttribute("categoriesSize", categories.size());

        m.addAttribute("pageNo", categoryPagination.getNumber());
        m.addAttribute("pageSize", categoryPagination.getSize());
        m.addAttribute("totalElements", categoryPagination.getTotalElements());
        m.addAttribute("totalPages", categoryPagination.getTotalPages());
        m.addAttribute("isFirst", categoryPagination.isFirst());
        m.addAttribute("isLast", categoryPagination.isLast());

        return "admin/category";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category,
                               @RequestParam("file") MultipartFile file,
                               HttpSession session) {
        try {
            if (!file.isEmpty()) {
                String imageName = UUID.randomUUID().toString() + ".jpg";

                Path uploadDir = Paths.get("src/main/resources/static/img/category_img");

                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                Path imagePath = uploadDir.resolve(imageName);

                Files.copy(file.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

                category.setImageName(imageName);
            } else {
                category.setImageName("default.jpg");
            }

            if (categoryService.existCategory(category.getName())) {
                session.setAttribute("errorMsg", "Category name already exists");
            } else {
                Category savedCategory = categoryService.saveCategory(category);
                if (ObjectUtils.isEmpty(savedCategory)) {
                    session.setAttribute("errorMsg", "Something went wrong!");
                } else {
                    session.setAttribute("successMsg", "Category saved successfully!");
                }
            }

        } catch (IOException e) {
            session.setAttribute("errorMsg", "Erro ao salvar imagem: " + e.getMessage());
        }

        return "redirect:/admin/category";
    }

    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id, HttpSession session){
        Boolean deleteCategory = categoryService.deleteCategory(id);

        if(deleteCategory){
            session.setAttribute("successMsg", "Category deleted successfully!");
        } else {
            session.setAttribute("errorMsg", "Something went wrong!");
        }

        return "redirect:/admin/category";
    }

    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable int id, Model m) {
        m.addAttribute("category", categoryService.getCategoryById(id));
        return "admin/edit_category";
    }

    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
                                 HttpSession session) throws IOException {

        Category oldCategory = categoryService.getCategoryById(category.getId());
        String imageName = file.isEmpty() ? oldCategory.getImageName() : UUID.randomUUID().toString() + ".jpg";

        if(!ObjectUtils.isEmpty(category)){
            oldCategory.setName(category.getName());
            oldCategory.setIsActive(category.getIsActive());
            oldCategory.setImageName(imageName);
        }

        Category updatedCategory = categoryService.saveCategory(oldCategory);

        if(!ObjectUtils.isEmpty(updatedCategory)){
            if(!file.isEmpty()){
                Path uploadDir = Paths.get("src/main/resources/static/img/category_img");

                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                Path imagePath = uploadDir.resolve(imageName);

                Files.copy(file.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("successMsg", "Category updated successfully!");
        } else {
            session.setAttribute("errorMsg", "Something went wrong!");
        }

        return "redirect:/admin/loadEditCategory/" + category.getId();
    }

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image, HttpSession session) throws IOException{

        String imageName = image.isEmpty() ? "default.jpg" : UUID.randomUUID().toString() + ".jpg";
        product.setImage(imageName);
        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice());

        Product savedProduct = productService.saveProduct(product);

        if (!ObjectUtils.isEmpty(savedProduct)) {
            if (!image.isEmpty()) {
                Path uploadDir = Paths.get("src/main/resources/static/img/product_img");

                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                Path imagePath = uploadDir.resolve(imageName);

                Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
            }

            session.setAttribute("successMsg", "Product saved successfully!");
        } else {
            session.setAttribute("errorMsg", "Something went wrong!");
        }

        return "redirect:/admin/loadAddProduct";
    }

    @GetMapping("/products")
    public String loadViewProduct(Model m,
                                  @RequestParam(value = "ch", defaultValue = "") String ch,
                                  @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                  @RequestParam(name = "pageSize", defaultValue = "4") Integer pageSize) {

        Page<Product> products;

        if (ch != null && !ch.isEmpty()) {
            products = productService.searchProductPagination(pageNo, pageSize, ch);
        } else {
            products = productService.getAllProductsPagination(pageNo, pageSize);
        }

        m.addAttribute("products", products.getContent());
        m.addAttribute("pageNo", products.getNumber());
        m.addAttribute("pageSize", products.getSize());
        m.addAttribute("totalElements", products.getTotalElements());
        m.addAttribute("totalPages", products.getTotalPages());
        m.addAttribute("isFirst", products.isFirst());
        m.addAttribute("isLast", products.isLast());
        return "admin/products";
    }

    @GetMapping("/deleteProduct/{id}")
    public String loadViewProduct(@PathVariable int id, HttpSession session) {
        Boolean deleteProduct = productService.deleteProduct(id);

        if (deleteProduct) {
            session.setAttribute("successMsg", "Product deleted successfully!");
        } else {
            session.setAttribute("errorMsg", "Something went wrong!");
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/editProduct/{id}")
    public String editProduct(@PathVariable int id, Model m) {
        m.addAttribute("product", productService.getProductById(id));
        m.addAttribute("categories", categoryService.getAllCategory());
        return "admin/edit_product";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image, HttpSession session, Model m) {

        if (product.getDiscount() < 0 || product.getDiscount() > 100) {
            session.setAttribute("errorMsg", "Discount must be between 0 and 100");
        } else {

            Product updatedProduct = productService.updateProduct(product, image);

            if (!ObjectUtils.isEmpty(updatedProduct)) {
                session.setAttribute("successMsg", "Product updated successfully!");
            } else {
                session.setAttribute("errorMsg", "Something went wrong!");
            }
        }
        return "redirect:/admin/editProduct/" + product.getId();
    }

    @GetMapping("/users")
    public String getAllUsers(Model m) {
        List<UserDtls> users = userService.getUsers("ROLE_USER");
        m.addAttribute("users", users);
        return "/admin/users";
    }

    @GetMapping("/updateStatus")
    public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id, HttpSession session) {

        Boolean f = userService.updateAccountStatus(id, status);

        if (f) {
            session.setAttribute("successMsg", "User account status updated successfully!");
        } else {
            session.setAttribute("errorMsg", "Something went wrong while updating the user account status!");
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/orders")
    public String getAllOrders(Model m,
                               @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                               @RequestParam(name = "pageSize", defaultValue = "4") Integer pageSize)
    {
        Page<ProductOrder> orderPage = orderService.getAllOrdersPagination(pageNo, pageSize);
        m.addAttribute("orders", orderPage);
        m.addAttribute("srch", false);

        m.addAttribute("pageNo", orderPage.getNumber());
        m.addAttribute("pageSize", orderPage.getSize());
        m.addAttribute("totalElements", orderPage.getTotalElements());
        m.addAttribute("totalPages", orderPage.getTotalPages());
        m.addAttribute("isFirst", orderPage.isFirst());
        m.addAttribute("isLast", orderPage.isLast());
        return "/admin/orders";
    }

    @PostMapping("/update_order_status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {
        OrderStatus[] values = OrderStatus.values();
        String status = null;

        for (OrderStatus orderSt : values) {
            if (orderSt.getId().equals(st)) {
                status = orderSt.getName();
            }
        }

        ProductOrder updateOrder = orderService.updateOrderStatus(id, status);

        try {
            commomUtils.sendMailForProductOrder(updateOrder, status);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        if (ObjectUtils.isEmpty(updateOrder)) {
            session.setAttribute("successMsg", "Order status updated successfully!");
        } else {
            session.setAttribute("errorMsg", "Something went wrong while updating order status!");
        }

        return "redirect:/admin/orders";
    }

    @GetMapping("/search_order")
    public String searchProduct(@RequestParam String orderId,
                                Model m,
                                @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                @RequestParam(name = "pageSize", defaultValue = "4") Integer pageSize,
                                HttpSession session) {

        if (orderId != null && !orderId.isEmpty()) {

            ProductOrder order = orderService.getOrdersByOrderId(orderId.trim());

            if (ObjectUtils.isEmpty(order)) {
                session.setAttribute("errorMsg", "Order not found!");
                m.addAttribute("orderDtls", null);
            } else {
                m.addAttribute("orderDtls", order);
            }

            m.addAttribute("srch", true);

        } else {
            Page<ProductOrder> orderPage = orderService.getAllOrdersPagination(pageNo, pageSize);
            m.addAttribute("orders", orderPage);
            m.addAttribute("srch", false);

            m.addAttribute("pageNo", orderPage.getNumber());
            m.addAttribute("pageSize", orderPage.getSize());
            m.addAttribute("totalElements", orderPage.getTotalElements());
            m.addAttribute("totalPages", orderPage.getTotalPages());
            m.addAttribute("isFirst", orderPage.isFirst());
            m.addAttribute("isLast", orderPage.isLast());
        }
        return "admin/orders";
    }

    @GetMapping("/addAdmin")
    public String loadAddAdmin(){
        return "/admin/add_admin";
    }

    @PostMapping("/saveAdmin")
    public String saveUser(@ModelAttribute UserDtls user,
                           @RequestParam("img") MultipartFile image,
                           HttpSession session) {

        try {
            // Define nome da imagem
            String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
            user.setProfileImage(imageName);

            // Salva usuário no banco
            UserDtls savedUser = userService.saveAdmin(user);

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

                session.setAttribute("successMsg", "Admin saved successfully!");
            } else {
                session.setAttribute("errorMsg", "Something went wrong while saving the admin!");
            }

        } catch (IOException e) {
            e.printStackTrace();
            session.setAttribute("errorMsg", "Failed to save image: " + e.getMessage());
        }

        return "redirect:/admin/addAdmin";
    }
}
