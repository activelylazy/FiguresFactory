package com.example.order.model;

import java.math.BigDecimal;

public interface ExchangeRate {

    BigDecimal applyCurrencyConversion(BigDecimal amount);

}
