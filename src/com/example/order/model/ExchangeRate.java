package com.example.order.model;

import java.math.BigDecimal;

public class ExchangeRate {

	private BigDecimal rate;
	public ExchangeRate(BigDecimal rate) {
		this.rate = rate;
	}
	
    public BigDecimal applyCurrencyConversion(BigDecimal amount) {
    	return amount.multiply(rate).setScale(0, BigDecimal.ROUND_HALF_UP);
    }

}
