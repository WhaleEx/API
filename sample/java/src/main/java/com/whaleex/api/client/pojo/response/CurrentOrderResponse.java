package com.whaleex.api.client.pojo.response;


public class CurrentOrderResponse {

    private String orderId;

    private short symbolId;
    private byte status;
    /** 委托价 **/
    private String price;
    /** 委托量 **/
    private String origQty;//
    /** 成交总额 **/
    private String execValue;//
    /** 成交均价 **/
    private String execAvgPrice;//
    /** 成交量 **/
    private String execQty; //成交量
    private long createTime;
    private String type;

    private long resignTime;    //重签名时间
    private byte side;

    private short takerFeeRate;
    private short makerFeeRate;

    @Override
    public String toString() {
        return "CurrentOrderResponse{" +
                "orderId='" + orderId + '\'' +
                ", symbolId=" + symbolId +
                ", status=" + status +
                ", price='" + price + '\'' +
                ", origQty='" + origQty + '\'' +
                ", execValue='" + execValue + '\'' +
                ", execAvgPrice='" + execAvgPrice + '\'' +
                ", execQty='" + execQty + '\'' +
                ", createTime=" + createTime +
                ", type='" + type + '\'' +
                ", resignTime=" + resignTime +
                ", side=" + side +
                ", takerFeeRate=" + takerFeeRate +
                ", makerFeeRate=" + makerFeeRate +
                '}';
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public short getSymbolId() {
        return symbolId;
    }

    public void setSymbolId(short symbolId) {
        this.symbolId = symbolId;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOrigQty() {
        return origQty;
    }

    public void setOrigQty(String origQty) {
        this.origQty = origQty;
    }

    public String getExecValue() {
        return execValue;
    }

    public void setExecValue(String execValue) {
        this.execValue = execValue;
    }

    public String getExecAvgPrice() {
        return execAvgPrice;
    }

    public void setExecAvgPrice(String execAvgPrice) {
        this.execAvgPrice = execAvgPrice;
    }

    public String getExecQty() {
        return execQty;
    }

    public void setExecQty(String execQty) {
        this.execQty = execQty;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getResignTime() {
        return resignTime;
    }

    public void setResignTime(long resignTime) {
        this.resignTime = resignTime;
    }

    public byte getSide() {
        return side;
    }

    public void setSide(byte side) {
        this.side = side;
    }

    public short getTakerFeeRate() {
        return takerFeeRate;
    }

    public void setTakerFeeRate(short takerFeeRate) {
        this.takerFeeRate = takerFeeRate;
    }

    public short getMakerFeeRate() {
        return makerFeeRate;
    }

    public void setMakerFeeRate(short makerFeeRate) {
        this.makerFeeRate = makerFeeRate;
    }
}
