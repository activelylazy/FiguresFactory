package com.example.order.processing;

import com.example.order.model.Currency;

public interface CurrencyCache {

	public Currency lookupCurrency(String currencyId);
	
}
