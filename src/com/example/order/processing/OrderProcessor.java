package com.example.order.processing;

import java.util.Date;

import com.example.order.OrderProcessingException;
import com.example.order.model.Trade;
import com.example.order.service.TradeFactory;
import com.google.inject.Inject;

public class OrderProcessor {

    private final FiguresFactory figuresFactory;
    private final TradeFactory tradeFactory;
    
    @Inject
    public OrderProcessor(FiguresFactory figuresFactory,
                          TradeFactory tradeFactory) {
        this.figuresFactory = figuresFactory;
        this.tradeFactory = tradeFactory;
    }
    
    public void processOrder(TradeOrder order) throws OrderProcessingException {
        Figures figures = figuresFactory.buildFrom(order, calculateEffectiveDateFor(order));
        
        Trade trade = tradeFactory.createTrade(figures, order.getAsset());
        trade.save();
    }
    
    private Date calculateEffectiveDateFor(TradeOrder order) {
        return new Date();
    }
    
}
