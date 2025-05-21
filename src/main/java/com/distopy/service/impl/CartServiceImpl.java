package com.distopy.service.impl;

import com.distopy.model.Cart;
import com.distopy.model.Product;
import com.distopy.model.UserDtls;
import com.distopy.repository.CartRepository;
import com.distopy.repository.ProductRepository;
import com.distopy.repository.UserRepository;
import com.distopy.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Cart saveCart(Integer productId, Integer userId) {

        UserDtls userDtls = userRepository.findById(userId).get();
        Product product = productRepository.findById(productId).get();

        Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);
        Cart cart = null;

        if (ObjectUtils.isEmpty(cartStatus)) {

            cart= new Cart();
            cart.setProduct(product);
            cart.setUser(userDtls);
            cart.setQuantity(1);
            cart.setTotalPrice(1 * product.getDiscountPrice());
        } else {
            cart = cartStatus;
            cart.setQuantity(cart.getQuantity() + 1);
            cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscountPrice());
        }
        return cartRepository.save(cart);
    }

    @Override
    public List<Cart> getCartsByUserId(Integer userId) {
        return null;
    }
}
