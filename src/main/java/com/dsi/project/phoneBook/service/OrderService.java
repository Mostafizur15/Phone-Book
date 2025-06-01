package com.dsi.project.phoneBook.service;

import com.dsi.project.phoneBook.entities.Order;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {

    public Order createOrder(JSONObject ob) {
        String id= UUID.randomUUID().toString();
        return new Order(id,ob.optInt("amount"),ob.optString("currency")
                                ,ob.optString("receipt"),"CREATED");

    }
}
