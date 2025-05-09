package com.distopy.repository;

import com.distopy.model.Category;
import com.distopy.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    public List<Product> findByIsActiveTrue();

    public List<Product> findByCategory(String category);
}
