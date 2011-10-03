package com.example.order.processing;

import org.hibernate.Session;

import com.example.order.model.HedgeFundAsset;
import com.timgroup.karg.keywords.KeywordArguments;

public class TradeOrderDAO {

	private Session session;
	private final TradeOrder.Factory tradeOrderFactory;

	public TradeOrderDAO(Session session, TradeOrder.Factory tradeOrderFactory) {
		this.session = session;
		this.tradeOrderFactory = tradeOrderFactory;
	}
	
	public TradeOrder createForAsset(HedgeFundAsset asset, KeywordArguments arguments) {
		TradeOrderRecord record = new TradeOrderRecord(arguments.with(TradeOrderRecord.Arguments.ASSET_ID.of(asset.getId())));
		session.save(record);
		return tradeOrderFactory.create(record, asset);
	}
}
