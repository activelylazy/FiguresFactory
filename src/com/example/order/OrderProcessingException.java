package com.example.order;

public class OrderProcessingException extends Exception {
    private static final long serialVersionUID = -3379659142002887600L;

    public OrderProcessingException(String msg) {
        super(msg);
    }

}
