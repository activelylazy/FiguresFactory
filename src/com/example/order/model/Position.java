package com.example.order.model;

import java.math.BigDecimal;

public class Position {

	private BigDecimal shareCount;

	public Position(BigDecimal shareCount) {
		this.shareCount = shareCount;
	}
	
    public BigDecimal getShareCount() {
    	return this.shareCount;
    }

}
