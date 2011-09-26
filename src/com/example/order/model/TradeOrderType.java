package com.example.order.model;

public enum TradeOrderType {
    SUBSCRIPTION("Subscription"),   // Buy the asset
    REDEMPTION("Redemption");       // Sell the asset

    private final String description;

    private TradeOrderType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    public boolean is(String orderType) {
        return description.equalsIgnoreCase(orderType);
    }

    public static TradeOrderType describedAs(String description) {
        for (TradeOrderType value : values()) {
            if (value.is(description)) {
                return value;
            }
        }
        return null;
    }

}
