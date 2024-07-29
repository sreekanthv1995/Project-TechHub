package com.tech_hub.techhub.service.razorpay;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.tech_hub.techhub.entity.TransactionDetails;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RazorPayService {

    private static final String KEY ="rzp_test_J4LYX9mptrIIqg";
    private static final String SECRET_KEY = "XU4WWENN7SVKHOBr6zYKiTU4";
    private static final String CURRENCY = "INR";



    public TransactionDetails createTransaction(Double amount){
        try {
            JSONObject jsonObject = new JSONObject();
            RazorpayClient razorpayClient = new RazorpayClient(KEY,SECRET_KEY);

            jsonObject.put("amount",(amount * 100));
            jsonObject.put("currency",CURRENCY);

            Order order = razorpayClient.orders.create(jsonObject);

            return prepareTransactionDetails(order);

        }catch (Exception e){
            log.error(e.getMessage());
        }
        return null;
    }

    private TransactionDetails prepareTransactionDetails(Order order){
        String orderId = order.get("id");
        String currency = order.get("currency");
        Integer amount = order.get("amount");


        return new TransactionDetails(orderId,currency,amount,KEY);
    }
}
