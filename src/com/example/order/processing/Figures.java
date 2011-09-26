package com.example.order.processing;

import java.math.BigDecimal;

import com.example.order.model.Currency;
import com.example.order.model.Price;


/**
 * The triple of (amount, shares, price). Ensures that they stay in sync.
 */
public class Figures {
    private final BigDecimal amount;
    private final BigDecimal shares;
    private final Price price;

    private Figures(BigDecimal amount, BigDecimal shares, Price price) {
        this.amount = amount;
        this.shares = shares;
        this.price = price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getShares() {
        return shares;
    }

    public Price getPrice() {
        return price;
    }
    
    public static Figures sharesAndPrice(BigDecimal shares, Price price) {
        return new Figures(shares.multiply(price.getValue()), shares, price);
    }
    
    public static Figures amountAndPrice(BigDecimal amount, Price price) {
        return new Figures(amount, amount.divide(price.getValue()), price);
    }

    public static Figures amountAndShares(BigDecimal amount, Currency currency, BigDecimal shares) {
        return new Figures(amount, shares, new Price(amount.divide(shares), currency));
    }
    
    public static Figures amountAndShares(String amount, Currency currency, String shares) {
        return amountAndShares(new BigDecimal(amount), currency, new BigDecimal(shares));
    }
    
}