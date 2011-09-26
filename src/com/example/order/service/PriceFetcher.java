package com.example.order.service;

import java.math.BigDecimal;
import java.util.Date;

import com.example.order.model.HedgeFundAsset;

public interface PriceFetcher {

    BigDecimal fetchBestPriceFor(HedgeFundAsset asset, Date tradeDate, BigDecimal fallbackbestPrice);

}
