package com.example.order.processing;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.example.order.OrderProcessingException;
import com.example.order.model.Currency;
import com.example.order.model.HedgeFundAsset;
import com.example.order.service.FXService;
import com.example.order.service.PositionFetcher;
import com.example.order.service.PriceFetcher;

public class FiguresFactoryTest {

    private static final SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
    
    private Mockery context = new Mockery() {{ setImposteriser(ClassImposteriser.INSTANCE); }};

    private final PriceFetcher priceFetcher = context.mock(PriceFetcher.class);
    private final PositionFetcher positionsFetcher = context.mock(PositionFetcher.class);
    private final FXService fxService = context.mock(FXService.class);
    private final HedgeFundAsset asset = context.mock(HedgeFundAsset.class);
    private final Currency gbp = context.mock(Currency.class);
    private Date effectiveDate;
    
    @Before public void
    setup_prices_and_assets() throws ParseException {
        context.checking(new Expectations() {{
            // best price for the asset is 5 GBP
            allowing(priceFetcher).fetchBestPriceFor(with(sameInstance(asset)), with(any(Date.class)), with(any(BigDecimal.class))); will(returnValue(new BigDecimal("5")));
            allowing(asset).getCurrency(); will(returnValue(gbp));
            allowing(gbp).getSymbol(); will(returnValue("GBP"));
        }});
        effectiveDate = fmt.parse("01/09/2011");
    }
    
    @Test
    public void creates_figures_for_an_order_with_an_amount() throws OrderProcessingException {
        TradeOrder order = new TradeOrder();
        order.setAmount(new BigDecimal("100"));
        order.setCurrency(gbp);
        order.setAsset(asset);
        
        Figures figures = new FiguresFactory(priceFetcher, positionsFetcher, fxService).buildFrom(order, effectiveDate);
        
        assertThat(figures.getAmount(), is(new BigDecimal("100")));
        assertThat(figures.getPrice().getValue(), is(new BigDecimal("5")));
        assertThat(figures.getPrice().getCurrency().getSymbol(), is("GBP"));
        assertThat(figures.getShares(), is(new BigDecimal("20")));
    }

}
