package com.distopy.service.impl;

import com.distopy.model.Cart;
import com.distopy.model.OrderAddress;
import com.distopy.model.OrderRequest;
import com.distopy.model.ProductOrder;
import com.distopy.repository.CartRepository;
import com.distopy.repository.ProductOrderRepository;
import com.distopy.service.OrderService;
import com.distopy.util.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductOrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Override
    public void saveOrder(Integer userId, OrderRequest orderRequest) {
        List<Cart> carts = cartRepository.findByUserId(userId);

        for (Cart cart : carts) {
            ProductOrder order = new ProductOrder();
            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(new Date());

            order.setProduct(cart.getProduct());
            order.setPrice(cart.getProduct().getDiscountPrice());

            order.setQuantity(cart.getQuantity());
            order.setUser(cart.getUser());

            order.setStatus(OrderStatus.IN_PROGRESS.getName());
            order.setPaymentType(orderRequest.getPaymentType());

            OrderAddress address = new OrderAddress();
            address.setFirstName(orderRequest.getFirstName());
            address.setLastName(orderRequest.getLastName());

            address.setEmail(orderRequest.getEmail());
            address.setMobileNumber(orderRequest.getMobileNumber());

            address.setAddress(orderRequest.getAddress());
            address.setCity(orderRequest.getCity());

            address.setState(orderRequest.getState());
            address.setPincode(orderRequest.getPincode());

            order.setOrderAddress(address);

            orderRepository.save(order);
        }
    }
}
