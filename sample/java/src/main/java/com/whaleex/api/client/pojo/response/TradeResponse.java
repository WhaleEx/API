package com.whaleex.api.client.pojo.response;


public class TradeResponse {
    private String orderId;
    private String execId;
    private byte type;
    private String quoteCurrencyName;
    private String baseCurrencyName;
    private String price;
    private String quantity;
    private String volume;
    private byte side;
    private String status;
    private String feeQty;
    private long feeCurrencyId;
    private String feeCurrency;
    private byte liquidityFlag;
    private long timestamp;


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getExecId() {
        return execId;
    }

    public void setExecId(String execId) {
        this.execId = execId;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getQuoteCurrencyName() {
        return quoteCurrencyName;
    }

    public void setQuoteCurrencyName(String quoteCurrencyName) {
        this.quoteCurrencyName = quoteCurrencyName;
    }

    public String getBaseCurrencyName() {
        return baseCurrencyName;
    }

    public void setBaseCurrencyName(String baseCurrencyName) {
        this.baseCurrencyName = baseCurrencyName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public byte getSide() {
        return side;
    }

    public void setSide(byte side) {
        this.side = side;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFeeQty() {
        return feeQty;
    }

    public void setFeeQty(String feeQty) {
        this.feeQty = feeQty;
    }

    public long getFeeCurrencyId() {
        return feeCurrencyId;
    }

    public void setFeeCurrencyId(long feeCurrencyId) {
        this.feeCurrencyId = feeCurrencyId;
    }

    public String getFeeCurrency() {
        return feeCurrency;
    }

    public void setFeeCurrency(String feeCurrency) {
        this.feeCurrency = feeCurrency;
    }

    public byte getLiquidityFlag() {
        return liquidityFlag;
    }

    public void setLiquidityFlag(byte liquidityFlag) {
        this.liquidityFlag = liquidityFlag;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
