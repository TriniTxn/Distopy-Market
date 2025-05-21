package com.distopy.service;

import com.distopy.model.Cart;

import java.util.List;

public interface CartService {

    Cart saveCart(Integer productId, Integer userId);

    List<Cart> getCartsByUserId(Integer userId);

    Integer getCountCart(Integer userId);
}
