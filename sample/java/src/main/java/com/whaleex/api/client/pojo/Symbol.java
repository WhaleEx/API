package com.whaleex.api.client.pojo;

public class Symbol {
    String baseCurrency;
    String quoteCurrency;
    int basePrecision;
    int quotePrecision;
    String baseContract;
    String quoteContract;


    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public void setQuoteCurrency(String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public int getBasePrecision() {
        return basePrecision;
    }

    public void setBasePrecision(int basePrecision) {
        this.basePrecision = basePrecision;
    }

    public int getQuotePrecision() {
        return quotePrecision;
    }

    public void setQuotePrecision(int quotePrecision) {
        this.quotePrecision = quotePrecision;
    }

    public String getBaseContract() {
        return baseContract;
    }

    public void setBaseContract(String baseContract) {
        this.baseContract = baseContract;
    }

    public String getQuoteContract() {
        return quoteContract;
    }

    public void setQuoteContract(String quoteContract) {
        this.quoteContract = quoteContract;
    }
}
