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

public class FiguresFactory {
    
    private final PriceFetcher bestPriceFetcher;
    private final PositionFetcher hedgeFundAssetPositionsFetcher;
    private final FXService fxService;
    
    @Inject
    public FiguresFactory(PriceFetcher bestPriceFetcher, 
                          PositionFetcher hedgeFundAssetPositionsFetcher, 
                          FXService fxService) {
        this.bestPriceFetcher = bestPriceFetcher;
        this.hedgeFundAssetPositionsFetcher = hedgeFundAssetPositionsFetcher;
        this.fxService = fxService;
    }
    
    public Figures buildFrom(TradeOrder order, Date effectiveDate) throws OrderProcessingException {
        Date tradeDate = order.getTradeDate();
        HedgeFundAsset asset = order.getAsset();
        
        BigDecimal bestPrice = bestPriceFor(asset, tradeDate);
        
        return order.getType() == TradeOrderType.REDEMPTION 
            ? figuresFromPosition(order, 
                                       lookupPosition(asset, order.getFohf(), tradeDate),
                                       lookupPosition(asset, order.getFohf(), effectiveDate),
                                       bestPrice) 
            : getFigures(order, bestPrice, null);
    }
    
    private BigDecimal bestPriceFor(HedgeFundAsset asset, Date tradeDate) {
        BigDecimal fallbackbestPrice = BigDecimal.ONE;
        BigDecimal bestPrice = bestPriceFetcher.fetchBestPriceFor(asset, tradeDate, fallbackbestPrice);
        return bestPrice;
    }
    
    private Figures getFigures(TradeOrder order, BigDecimal assetPrice, BigDecimal totalSharesSubscribed) throws OrderProcessingException {
        Currency assetCurrency = order.getAsset().getCurrency();
        BigDecimal amount = order.getAmount();
        
        if (shouldConvertPaymentCurrency(order, order.getAsset())) {
            ExchangeRate rate = getRateFromPaymentCurrencyToAssetCurrency(order.getFohf(), order.getTradeDate(), order.getCurrency(), assetCurrency);
            if (null != amount) {
                amount = rate.applyCurrencyConversion(amount);
            }
        }
        
        Price price = Price.price(assetPrice, assetCurrency);
        if (amount != null) {
            return Figures.amountAndPrice(amount, price);
        }
        if (order.getShares() != null) {
            return Figures.sharesAndPrice(order.getShares(), price);
        }
        if (order.getPercentage() != null && totalSharesSubscribed != null) {
            return Figures.sharesAndPrice(totalSharesSubscribed.multiply(order.getPercentage()).divide(BigDecimal.valueOf(100)), price);
        }
        throw new OrderProcessingException("No figures created");
    }
    
    private Position lookupPosition(HedgeFundAsset asset, FundOfFund fohf, Date day) {
        return hedgeFundAssetPositionsFetcher.getAssetPosition(asset, fohf, day);
    }

    private Figures figuresFromPosition(TradeOrder order, 
                                             Position positionOnTradeDate,
                                             Position positionOnEffectiveDate,
                                             BigDecimal bestPrice) throws OrderProcessingException {
        Figures tradeFigures = getFigures(order, bestPrice, positionOnTradeDate.getShareCount());
        
        if (positionOnEffectiveDate.getShareCount().compareTo(tradeFigures.getShares()) < 0) {
            throw new OrderProcessingException("Redemption is for more than total number of shares available");
        }
        return tradeFigures;
    }

    private boolean shouldConvertPaymentCurrency(TradeOrder order, HedgeFundAsset asset) {
        if (order.getCurrency().equals(asset.getCurrency())) {
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