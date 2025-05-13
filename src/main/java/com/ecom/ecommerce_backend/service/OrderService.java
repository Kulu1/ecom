package com.ecom.ecommerce_backend.service;

import com.ecom.ecommerce_backend.model.LocalUser;
import com.ecom.ecommerce_backend.model.WebOrder;
import com.ecom.ecommerce_backend.model.dao.WebOrderDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private WebOrderDAO webOrderDAO;

    public OrderService(WebOrderDAO weborderDAO){
        this.webOrderDAO = weborderDAO;
    }

    public List<WebOrder> getOrders(LocalUser user){
        return webOrderDAO.findByUser(user);
    }


}
