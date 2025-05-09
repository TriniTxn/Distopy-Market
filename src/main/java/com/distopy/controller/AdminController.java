package com.distopy.controller;

import com.distopy.model.Category;
import com.distopy.model.Product;
import com.distopy.service.CategoryService;
import com.distopy.service.ProductService;
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
import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;


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
    public String category(Model m){
        m.addAttribute("categories", categoryService.getAllCategory());
        return "admin/category";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category,
                               @RequestParam("file") MultipartFile file,
                               HttpSession session) {

        try {
            if (!file.isEmpty()) {
                // Cria o nome da imagem baseado no nome da categoria
                String imageName = category.getName().trim().replaceAll("\\s+", "_") + ".jpg";

                // Caminho real onde a imagem será salva
                Path imagePath = Paths.get("src/main/resources/static/img/category_img/" + imageName);

                // Salva a imagem no diretório
                Files.copy(file.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

                category.setImageName(imageName);
            } else {
                category.setImageName("default.jpg");
            }

            // Verifica se já existe categoria com mesmo nome
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
        String filename = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();

        if(!ObjectUtils.isEmpty(category)){

            oldCategory.setName(category.getName());
            oldCategory.setIsActive(category.getIsActive());
            oldCategory.setImageName(filename);
        }

        Category updatedCategory = categoryService.saveCategory(oldCategory);

        if(!ObjectUtils.isEmpty(updatedCategory)){

            if(!file.isEmpty()){
                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + "/category_img/" + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("successMsg", "Category updated successfully!");
        } else {
            session.setAttribute("errorMsg", "Something went wrong!");
        }

        return "redirect:/admin/loadEditCategory/" + category.getId();
    }

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image, HttpSession session) throws IOException{

        String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
        product.setImage(imageName);
        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice());

        Product savedProduct = productService.saveProduct(product);

        if (!ObjectUtils.isEmpty(savedProduct)) {

            File savedImg = new ClassPathResource("static/img").getFile();
            File productImgDir = new File(savedImg, "product_img");

            if (!productImgDir.exists()) {
                productImgDir.mkdirs();
            }

            Path path = Paths.get(productImgDir.getAbsolutePath(), image.getOriginalFilename());
            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            session.setAttribute("successMsg", "Product saved successfully!");
        } else {
            session.setAttribute("errorMsg", "Something went wrong!");
        }

        return "redirect:/admin/loadAddProduct";
    }

    @GetMapping("/products")
    public String loadViewProduct(Model m) {
        m.addAttribute("products", productService.getAllProducts());
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
}
