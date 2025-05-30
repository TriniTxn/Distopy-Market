package com.distopy.service.impl;

import com.distopy.model.Product;
import com.distopy.repository.ProductRepository;
import com.distopy.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.multipart.MultipartFile;

import java.beans.PropertyEditorSupport;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Double.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text != null && !text.isEmpty()) {
                    setValue(Double.parseDouble(text.replace(",", ".")));
                } else {
                    setValue(null);
                }
            }
        });
    }


    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Boolean deleteProduct(Integer id) {
        Product product = productRepository.findById(id).orElse(null);

        if (!ObjectUtils.isEmpty(product)) {
            productRepository.delete(product);
            return true;
        }
        return false;
    }

    @Override
    public Product getProductById(Integer id) {
        Product product = productRepository.findById(id).orElse(null);
        return product;
    }

    @Override
    public Product updateProduct(Product product, MultipartFile image) {

        Product dbProduct = getProductById(product.getId());

        String imageName = image.isEmpty() ? dbProduct.getImage() : image.getOriginalFilename();

        dbProduct.setTitle(product.getTitle());
        dbProduct.setDescription(product.getDescription());
        dbProduct.setCategory(product.getCategory());
        dbProduct.setPrice(product.getPrice());
        dbProduct.setStock(product.getStock());
        dbProduct.setImage(imageName);
        dbProduct.setDiscount(product.getDiscount());

        Double discount = product.getPrice() * (product.getDiscount() / 100.0);
        Double discountPrice = product.getPrice() - discount;

        dbProduct.setDiscountPrice(discountPrice);
        System.out.println("Preço: " + product.getPrice());
        System.out.println("Desconto: " + product.getDiscount());
        System.out.println("Preço com desconto: " + discountPrice);
        dbProduct.setIsActive(product.getIsActive());

        Product updatedProduct = productRepository.save(dbProduct);

        if (!ObjectUtils.isEmpty(updatedProduct)) {

            if(!image.isEmpty()){
                try {
                    String customImageName = UUID.randomUUID().toString() + ".jpg";

                    Path uploadDir = Paths.get("src/main/resources/static/img/product_img");

                    if (!Files.exists(uploadDir)) {
                        Files.createDirectories(uploadDir);
                    }
                    Path imagePath = uploadDir.resolve(customImageName);

                    Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

                    dbProduct.setImage(customImageName);
                    updatedProduct = productRepository.save(dbProduct);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return updatedProduct;
        }
        return null;
    }

    @Override
    public List<Product> getAllActiveProducts(String category) {
        List<Product> products = null;
        if (ObjectUtils.isEmpty(category)) {
            return productRepository.findByIsActiveTrue();
        } else {
            return productRepository.findByCategory(category);
        }
    }

    @Override
    public List<Product> getSearchedProduct(String ch) {
        return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch);
    }


}
