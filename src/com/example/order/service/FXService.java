package com.example.order.service;

import java.util.Date;

import com.example.order.CurrencyException;
import com.example.order.model.Currency;
import com.example.order.model.ExchangeRate;

public interface FXService {

    ExchangeRate getExchangeRate(Currency orderCurrency, Currency assetCurrency, Date tradeDate) throws CurrencyException;

}
