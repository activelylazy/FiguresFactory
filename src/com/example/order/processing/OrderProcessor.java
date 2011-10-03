package com.example.order.processing;

import java.util.Date;

import com.example.order.OrderProcessingException;
import com.example.order.model.FundOfFund;
import com.example.order.model.Trade;
import com.example.order.service.TradeFactory;
import com.google.inject.Inject;

public class OrderProcessor {

    private final TradeFactory tradeFactory;
    
    @Inject
    public OrderProcessor(TradeFactory tradeFactory) {
        this.tradeFactory = tradeFactory;
    }
    
    public void processOrder(TradeOrder order, FundOfFund fohf) throws OrderProcessingException {
        Figures figures = order.buildFrom(calculateEffectiveDateFor(order), fohf);
        
        Trade trade = tradeFactory.createTrade(figures, order.getAsset());
        trade.save();
    }
    
    private Date calculateEffectiveDateFor(TradeOrder order) {
        return new Date();
    }
    
}
