package com.distopy.service;

import com.distopy.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    Product saveProduct(Product product);

    List<Product> getAllProducts();

    Boolean deleteProduct(Integer id);

    Product getProductById(Integer id);

    Product updateProduct(Product product, MultipartFile file);

    List<Product> getAllActiveProducts(String category);

    List<Product> getSearchedProduct(String ch);

    Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize, String category);
}
