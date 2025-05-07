package com.distopy.service;

import com.distopy.model.Product;

import java.util.List;

public interface ProductService {

    public Product saveProduct(Product product);

    public List<Product> getAllProducts();

    public Boolean deleteProduct(Integer id);
}
