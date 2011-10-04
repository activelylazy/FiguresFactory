package com.example.order.processing;

import java.math.BigDecimal;
import java.util.Date;

import com.example.order.CurrencyException;
import com.example.order.OrderProcessingException;
import com.example.order.model.Currency;
import com.example.order.model.ExchangeRate;
import com.example.order.model.FundOfFund;
import com.example.order.model.HedgeFundAsset;
import com.example.order.model.Position;
import com.example.order.model.Price;
import com.example.order.model.TradeOrderType;
import com.example.order.service.FXService;
import com.example.order.service.PositionFetcher;
import com.example.order.service.PriceFetcher;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class TradeOrder {
    
    private final String id;
    private final String companyId;
    private final FundOfFund fohf;
    private final String currencyId;
    private final HedgeFundAsset asset;
    private final String status;
    private final BigDecimal amount;
    private final BigDecimal shares;
    private final BigDecimal percentage;
    private final boolean isWholeHedgeFund;
    private final Date tradeDate;
    private final Date valueDate;
    private final TradeOrderType type;
    
	private final CurrencyCache currencyCache;
	
    private PriceFetcher bestPriceFetcher;
    private PositionFetcher hedgeFundAssetPositionsFetcher;
    private FXService fxService;
	
    public static interface Factory {
    	TradeOrder create(TradeOrderRecord record);
    }
    
    @Inject
	public TradeOrder(CurrencyCache currencyCache,
					  PriceFetcher bestPriceFetcher,
					  PositionFetcher hedgeFundAssetPositionsFetcher,
					  FXService fxService,
					  @Assisted TradeOrderRecord record) {
		this.bestPriceFetcher = bestPriceFetcher;
		this.hedgeFundAssetPositionsFetcher = hedgeFundAssetPositionsFetcher;
		this.fxService = fxService;
    	this.currencyCache = currencyCache;
		this.id = record.id;
    	this.companyId = record.companyId;
    	this.fohf = record.fohf;
    	this.asset = record.asset;
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
    
    public Figures buildFrom(Date effectiveDate) throws OrderProcessingException {
        BigDecimal bestPrice = bestPriceFor(this.asset, this.tradeDate);
        
        return this.type == TradeOrderType.REDEMPTION 
            ? figuresFromPosition(fohf,
                                  lookupPosition(this.asset, fohf, this.tradeDate),
                                  lookupPosition(this.asset, fohf, effectiveDate), bestPrice) 
            : getFigures(fohf, bestPrice, null);
    }
    
    @Deprecated
    public HedgeFundAsset getAsset() { return this.asset; }
    
    private Currency getCurrency() {
    	return this.currencyCache.lookupCurrency(this.currencyId);
    }

    private BigDecimal bestPriceFor(HedgeFundAsset asset, Date tradeDate) {
        BigDecimal fallbackbestPrice = BigDecimal.ONE;
        BigDecimal bestPrice = bestPriceFetcher.fetchBestPriceFor(asset, tradeDate, fallbackbestPrice);
        return bestPrice;
    }
    
    private Figures getFigures(FundOfFund fohf, BigDecimal assetPrice, BigDecimal totalSharesSubscribed) throws OrderProcessingException {
        Currency assetCurrency = this.asset.getCurrency();
        BigDecimal amount = this.amount;
        
        if (shouldConvertPaymentCurrency(this.asset)) {
            ExchangeRate rate = getRateFromPaymentCurrencyToAssetCurrency(fohf, this.tradeDate, this.getCurrency(), assetCurrency);
            if (null != amount) {
                amount = rate.applyCurrencyConversion(amount);
            }
        }
        
        Price price = Price.price(assetPrice, assetCurrency);
        if (amount != null) {
            return Figures.amountAndPrice(amount, price);
        }
        if (this.shares != null) {
            return Figures.sharesAndPrice(this.shares, price);
        }
        if (this.percentage != null && totalSharesSubscribed != null) {
            return Figures.sharesAndPrice(totalSharesSubscribed.multiply(this.percentage).divide(BigDecimal.valueOf(100)), price);
        }
        throw new OrderProcessingException("No figures created");
    }
    
    private Position lookupPosition(HedgeFundAsset asset, FundOfFund fohf, Date day) {
        return hedgeFundAssetPositionsFetcher.getAssetPosition(asset, fohf, day);
    }

    private Figures figuresFromPosition(FundOfFund fohf,
                                        Position positionOnTradeDate,
                                        Position positionOnEffectiveDate, BigDecimal bestPrice) throws OrderProcessingException {
        Figures tradeFigures = getFigures(fohf, bestPrice, positionOnTradeDate.getShareCount());
        
        if (positionOnEffectiveDate.getShareCount().compareTo(tradeFigures.getShares()) < 0) {
            throw new OrderProcessingException("Redemption is for more than total number of shares available");
        }
        return tradeFigures;
    }

    private boolean shouldConvertPaymentCurrency(HedgeFundAsset asset) {
        if (this.getCurrency().equals(asset.getCurrency())) {
            return false;
        }
        return true;
    }

    private ExchangeRate getRateFromPaymentCurrencyToAssetCurrency(FundOfFund fohf, Date tradeDate, Currency orderCurrency, Currency assetCurrency) throws OrderProcessingException {
        try {
            return fxService.getExchangeRate(orderCurrency, assetCurrency, tradeDate);
        } catch (CurrencyException e) {
            throw new OrderProcessingException(String.format("No exchange rate found from %s to %s", orderCurrency.getSymbol(), assetCurrency.getSymbol()));
        }
    }

}