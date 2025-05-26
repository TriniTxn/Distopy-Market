package com.distopy.service;

import com.distopy.model.OrderRequest;
import com.distopy.model.Product;
import com.distopy.model.ProductOrder;

import java.util.List;

public interface OrderService {

    void saveOrder(Integer userId, OrderRequest orderRequest);

    List<ProductOrder> getOrdersByUserId(Integer userId);

    Boolean updateOrderStatus(Integer orderId, String status);
}
