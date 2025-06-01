package com.dsi.project.phoneBook.controller;

import com.dsi.project.phoneBook.entities.Order;
import com.dsi.project.phoneBook.service.OrderService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class PaymentController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create_order")
    public Object createOrder(Model model, @RequestBody Map<String,Object>data) {
        System.out.println("createOrder"+data);
        long amount=Long.parseLong(data.get("amount").toString());
        System.out.println("amount"+amount);
        JSONObject ob = new JSONObject();
        ob.put("amount",amount);
        ob.put("currency","$");
        ob.put("receipt","121212");
        ob.put("payment_capture", 1);

        //creating order
            //Order order = client.Orders.create(ob);
            // Here we will use gateway api to create order. we pass object to that gateway
            // service and then it return order object;
        //We will create custome order
        Order order = this.orderService.createOrder(ob);
        System.out.println(order);
        return order;
    }
}
