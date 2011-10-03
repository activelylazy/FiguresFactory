package com.example.order.processing;

import java.math.BigDecimal;
import java.util.Date;

import com.example.order.model.Currency;
import com.example.order.model.TradeOrderType;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class TradeOrder {
    
    private final String id;
    private final String companyId;
    private final String fohfId;
    private final String assetId;
    private final String currencyId;
    private final String status;
    private final BigDecimal amount;
    private final BigDecimal shares;
    private final BigDecimal percentage;
    private final boolean isWholeHedgeFund;
    private final Date tradeDate;
    private final Date valueDate;
    private final TradeOrderType type;
    
	private final CurrencyCache currencyCache;
	
    @Inject
    public TradeOrder(CurrencyCache currencyCache,
    				  @Assisted TradeOrderRecord record) {
    	this.currencyCache = currencyCache;
		this.id = record.id;
    	this.companyId = record.companyId;
    	this.fohfId = record.fohfId;
    	this.assetId = record.assetId;
    	this.currencyId = record.currencyId;
    	this.status = record.status;
    	this.amount = record.amount;
    	this.shares = record.shares;
    	this.percentage = record.percentage;
    	this.isWholeHedgeFund = record.isWholeHedgeFund;
    	this.tradeDate = record.tradeDate;
    	this.valueDate = record.valueDate;
    	this.type = record.type;
    }
    
    public String getId() { return id; }
    public String getCompanyId() { return companyId; }
    public String getFohfId() { return fohfId; } 
    public String getAssetId() { return assetId; }
    public String getCurrencyId() { return currencyId; }
    public Currency getCurrency() {
    	return this.currencyCache.lookupCurrency(this.currencyId);
    }
    public String getStatus() { return status; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getShares() { return shares; }
    public BigDecimal getPercentage() { return percentage; }
    public boolean isWholeHedgeFund() { return isWholeHedgeFund; }
    public Date getTradeDate() { return tradeDate; }
    public Date getValueDate() { return valueDate; }
    public TradeOrderType getType() { return type; }

}