package com.example.order.processing;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.ForeignKey;

import com.example.order.model.Company;
import com.example.order.model.Currency;
import com.example.order.model.FundOfFund;
import com.example.order.model.HedgeFundAsset;
import com.example.order.model.Trade;
import com.example.order.model.TradeOrderType;


@Entity
@Table(name="TRADE_ORDER")
public class TradeOrder {
    
    @Id
    @Column(name="ID", length=32)
    @GeneratedValue
    private String id;

    @ManyToOne
    @JoinColumn(name="COMPANY_ID", nullable=false)
    @ForeignKey(name="FK_ORDER_COMPANY")
    @AccessType("field")
    private Company company;
    
    @ManyToOne
    @JoinColumn(name="FOHF_ID", nullable=false)
    @ForeignKey(name="FK_ORDER_FOHF")
    @AccessType("field")
    private FundOfFund fohf;
    
    @ManyToOne
    @JoinColumn(name="ASSET_ID", nullable=false)
    @ForeignKey(name="FK_ORDER_ASSET")
    @AccessType("field")
    private HedgeFundAsset asset;
    
    @ManyToOne
    @JoinColumn(name="CURRENCY_ID", nullable=false)
    @ForeignKey(name="FK_ORDER_CURRENCY")
    @AccessType("field")
    private Currency currency;
    
    @Column(name="STATUS", nullable=false)
    private String status;
    
    @Column(name="AMOUNT", nullable=true)
    private BigDecimal amount;
    
    @Column(name="SHARES", nullable=true)
    private BigDecimal shares;

    @Column(name="PERCENTAGE", nullable=true)
    private BigDecimal percentage;
    
    @Column(name="WHOLE_HEDGE_FUND", nullable=false)
    private boolean isWholeHedgeFund;

    @Column(name="TRADE_DATE", nullable=false)
    private Date tradeDate;

    @Column(name="VALUE_DATE", nullable=false)
    private Date valueDate;

    @Column(name="TYPE")
    private TradeOrderType type;
    
    @OneToMany(mappedBy="order")
    private final Set<Trade> trades = new HashSet<Trade>();

    public TradeOrder() { }
    
    public TradeOrder(FundOfFund fohf,
                 HedgeFundAsset asset,
                 Currency currency,
                 String status,
                 BigDecimal amount,
                 BigDecimal shares,
                 BigDecimal percentage,
                 boolean isWholeHedgeFund,
                 Date tradeDate,
                 Date valueDate,
                 TradeOrderType type) {
        
        this.company = fohf.getCompany();
        this.fohf = fohf;
        this.asset = asset;
        this.currency = currency;
        this.status = status;
        this.amount = amount;
        this.shares = shares;
        this.percentage = percentage;
        this.isWholeHedgeFund = isWholeHedgeFund;
        this.tradeDate = tradeDate;
        this.valueDate = valueDate;
        this.type = type;
    }

    public String getId() { return id; }
    
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public FundOfFund getFohf() { return fohf; } 
    public void setFohf(FundOfFund fohf) { this.fohf = fohf; }

    public HedgeFundAsset getAsset() { return asset; }
    public void setAsset(HedgeFundAsset asset) { this.asset = asset; }

    public Currency getCurrency() { return currency; }
    public void setCurrency(Currency currency) { this.currency = currency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getShares() { return shares; }
    public void setShares(BigDecimal shares) { this.shares = shares; }

    public BigDecimal getPercentage() { return percentage; }
    public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }

    public boolean isWholeHedgeFund() { return isWholeHedgeFund; }
    public void setWholeHedgeFund(boolean wholeHedgeFund) { this.isWholeHedgeFund = wholeHedgeFund; }
    
    public Date getTradeDate() { return tradeDate; }
    public void setTradeDate(Date tradeDate) { this.tradeDate = tradeDate; }

    public Date getValueDate() { return valueDate; }
    public void setValueDate(Date valueDate) { this.valueDate = valueDate; }

    public TradeOrderType getType() { return type; }
    public void setType(TradeOrderType type) { this.type = type; }

    public void addTrade(Trade trade) {
        this.trades.add(trade);
    }
    
    public void addTrades(Iterable<Trade> additionalTrades) {
        for (Trade trade : additionalTrades) {
            addTrade(trade); 
        }
    }

}