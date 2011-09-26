package com.example.order.service;

import java.util.Date;

import com.example.order.model.FundOfFund;
import com.example.order.model.HedgeFundAsset;
import com.example.order.model.Position;

public interface PositionFetcher {

    Position getAssetPosition(HedgeFundAsset asset, FundOfFund fohf, Date day);

}
