package com.distopy.service;

import com.distopy.model.OrderRequest;
import com.distopy.model.ProductOrder;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface OrderService {

    void saveOrder(Integer userId, OrderRequest orderRequest) throws MessagingException, UnsupportedEncodingException;

    List<ProductOrder> getOrdersByUserId(Integer userId);

    ProductOrder updateOrderStatus(Integer orderId, String status);

    List<ProductOrder> getAllOrders();

    ProductOrder getOrdersByOrderId(String orderId);
}
