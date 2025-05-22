package com.distopy.service;

import com.distopy.model.OrderRequest;
import com.distopy.model.ProductOrder;

public interface OrderService {

    void saveOrder(Integer userId, OrderRequest orderRequest);
}
