package com.whaleex.api.client.pojo.response;

import java.util.List;

public class SimpleOrderBookResponse {
    private String lastUpdateId;
    private long timestamp;
    List<SimplePriceLevelResponse> asks;
    List<SimplePriceLevelResponse> bids;

    @Override
    public String toString() {
        return "SimpleOrderBookResponse{" +
                "lastUpdateId='" + lastUpdateId + '\'' +
                ", timestamp=" + timestamp +
                ", asks=" + asks +
                ", bids=" + bids +
                '}';
    }

    public String getLastUpdateId() {
        return lastUpdateId;
    }

    public void setLastUpdateId(String lastUpdateId) {
        this.lastUpdateId = lastUpdateId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<SimplePriceLevelResponse> getAsks() {
        return asks;
    }

    public void setAsks(List<SimplePriceLevelResponse> asks) {
        this.asks = asks;
    }

    public List<SimplePriceLevelResponse> getBids() {
        return bids;
    }

    public void setBids(List<SimplePriceLevelResponse> bids) {
        this.bids = bids;
    }
}
