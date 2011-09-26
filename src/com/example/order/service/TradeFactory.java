package com.example.order.service;

import com.example.order.model.HedgeFundAsset;
import com.example.order.model.Trade;
import com.example.order.processing.Figures;

public interface TradeFactory {

    Trade createTrade(Figures figures, HedgeFundAsset asset);
    
}
