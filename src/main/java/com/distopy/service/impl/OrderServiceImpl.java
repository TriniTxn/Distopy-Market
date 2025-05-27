package com.distopy.service.impl;

import com.distopy.model.Cart;
import com.distopy.model.OrderAddress;
import com.distopy.model.OrderRequest;
import com.distopy.model.ProductOrder;
import com.distopy.repository.CartRepository;
import com.distopy.repository.ProductOrderRepository;
import com.distopy.repository.ProductRepository;
import com.distopy.service.OrderService;
import com.distopy.util.CommomUtils;
import com.distopy.util.OrderStatus;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductOrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CommomUtils commomUtils;

    @Override
    public void saveOrder(Integer userId, OrderRequest orderRequest) throws MessagingException, UnsupportedEncodingException {
        List<Cart> carts = cartRepository.findByUserId(userId);

        for (Cart cart : carts) {
            ProductOrder order = new ProductOrder();
            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(LocalDate.now());

            order.setProduct(cart.getProduct());
            order.setPrice(cart.getProduct().getDiscountPrice());

            order.setQuantity(cart.getQuantity());
            order.setUser(cart.getUser());

            order.setStatus(OrderStatus.IN_PROGRESS.getName());
            order.setPaymentType(orderRequest.getPaymentType());

            OrderAddress address = getOrderAddress(orderRequest);

            order.setOrderAddress(address);

            ProductOrder savedOrder = orderRepository.save(order);
            commomUtils.sendMailForProductOrder(savedOrder, "Successful");
        }
    }

    private static OrderAddress getOrderAddress(OrderRequest orderRequest) {
        OrderAddress address = new OrderAddress();
        address.setFirstName(orderRequest.getFirstName());
        address.setLastName(orderRequest.getLastName());

        address.setEmail(orderRequest.getEmail());
        address.setMobileNumber(orderRequest.getMobileNumber());

        address.setAddress(orderRequest.getAddress());
        address.setCity(orderRequest.getCity());

        address.setState(orderRequest.getState());
        address.setPincode(orderRequest.getPincode());
        return address;
    }

    @Override
    public List<ProductOrder> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public ProductOrder updateOrderStatus(Integer orderId, String status) {
        Optional<ProductOrder> order = orderRepository.findById(orderId);

        if (order.isPresent()) {
            ProductOrder productOrder = order.get();
            productOrder.setStatus(status);
            ProductOrder updatedOrder = orderRepository.save(productOrder);
            return updatedOrder;
        }
        return null;
    }

    @Override
    public List<ProductOrder> getAllOrders() {
        return orderRepository.findAll();
    }
}
