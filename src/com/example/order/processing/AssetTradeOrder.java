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

public class AssetTradeOrder extends TradeOrder {

	private final HedgeFundAsset asset;
	
    private PriceFetcher bestPriceFetcher;
    private PositionFetcher hedgeFundAssetPositionsFetcher;
    private FXService fxService;

    public static interface Factory {
    	AssetTradeOrder create(TradeOrderRecord record, HedgeFundAsset asset);
    }
    
    @Inject
	public AssetTradeOrder(CurrencyCache currencyCache,
						   PriceFetcher bestPriceFetcher,
						   PositionFetcher hedgeFundAssetPositionsFetcher,
						   FXService fxService,
						   @Assisted TradeOrderRecord record,
						   @Assisted HedgeFundAsset asset) {
		super(currencyCache, record);
		this.bestPriceFetcher = bestPriceFetcher;
		this.hedgeFundAssetPositionsFetcher = hedgeFundAssetPositionsFetcher;
		this.fxService = fxService;
		this.asset = asset;
	}
	
	public HedgeFundAsset getAsset() { return this.asset; }
	
    public Figures buildFrom(Date effectiveDate, FundOfFund fohf) throws OrderProcessingException {
    	assert fohf.getId().equals(this.getFohfId());
    	
        BigDecimal bestPrice = bestPriceFor(this.getAsset(), this.getTradeDate());
        
        return this.getType() == TradeOrderType.REDEMPTION 
            ? figuresFromPosition(fohf,
                                  lookupPosition(this.getAsset(), fohf, this.getTradeDate()),
                                  lookupPosition(this.getAsset(), fohf, effectiveDate), bestPrice) 
            : getFigures(fohf, bestPrice, null);
    }
    
    private BigDecimal bestPriceFor(HedgeFundAsset asset, Date tradeDate) {
        BigDecimal fallbackbestPrice = BigDecimal.ONE;
        BigDecimal bestPrice = bestPriceFetcher.fetchBestPriceFor(asset, tradeDate, fallbackbestPrice);
        return bestPrice;
    }
    
    private Figures getFigures(FundOfFund fohf, BigDecimal assetPrice, BigDecimal totalSharesSubscribed) throws OrderProcessingException {
        Currency assetCurrency = this.getAsset().getCurrency();
        BigDecimal amount = this.getAmount();
        
        if (shouldConvertPaymentCurrency(this.getAsset())) {
            ExchangeRate rate = getRateFromPaymentCurrencyToAssetCurrency(fohf, this.getTradeDate(), this.getCurrency(), assetCurrency);
            if (null != amount) {
                amount = rate.applyCurrencyConversion(amount);
            }
        }
        
        Price price = Price.price(assetPrice, assetCurrency);
        if (amount != null) {
            return Figures.amountAndPrice(amount, price);
        }
        if (this.getShares() != null) {
            return Figures.sharesAndPrice(this.getShares(), price);
        }
        if (this.getPercentage() != null && totalSharesSubscribed != null) {
            return Figures.sharesAndPrice(totalSharesSubscribed.multiply(this.getPercentage()).divide(BigDecimal.valueOf(100)), price);
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
