package com.example.order.model;

import java.math.BigDecimal;


public class Price {
    private final BigDecimal amount;
    private final Currency currency;

    public Price(BigDecimal value, Currency currency) {
        this.amount = value;
        this.currency = currency;
    }
    
    public Currency getCurrency() {
        return this.currency;
    }

    public BigDecimal getValue() {
        return this.amount;
    }

    public static Price price(double price, Currency currency) {
        return price(new BigDecimal(price), currency);
    }
    
    public static Price price(BigDecimal price, Currency currency) {
        return new Price(price, currency);
    }
    
    @Override
    public String toString() {
        return "Price<" + amount + ", " + currency + ">";
    }

}
