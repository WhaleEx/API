package com.whaleex.api.client.pojo.response;


public class SymbolResponse{

    private String name;

    private long baseCurrencyId;
    private String baseCurrency;
    private short basePrecision;

    private short quotePrecision;
    private long quoteCurrencyId;
    private String quoteCurrency;


    @Override
    public String toString() {
        return "SymbolResponse{" +
                "name='" + name + '\'' +
                ", baseCurrencyId=" + baseCurrencyId +
                ", baseCurrency='" + baseCurrency + '\'' +
                ", basePrecision=" + basePrecision +
                ", quotePrecision=" + quotePrecision +
                ", quoteCurrencyId=" + quoteCurrencyId +
                ", quoteCurrency='" + quoteCurrency + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getBaseCurrencyId() {
        return baseCurrencyId;
    }

    public void setBaseCurrencyId(long baseCurrencyId) {
        this.baseCurrencyId = baseCurrencyId;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public short getBasePrecision() {
        return basePrecision;
    }

    public void setBasePrecision(short basePrecision) {
        this.basePrecision = basePrecision;
    }

    public short getQuotePrecision() {
        return quotePrecision;
    }

    public void setQuotePrecision(short quotePrecision) {
        this.quotePrecision = quotePrecision;
    }

    public long getQuoteCurrencyId() {
        return quoteCurrencyId;
    }

    public void setQuoteCurrencyId(long quoteCurrencyId) {
        this.quoteCurrencyId = quoteCurrencyId;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public void setQuoteCurrency(String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }
}
