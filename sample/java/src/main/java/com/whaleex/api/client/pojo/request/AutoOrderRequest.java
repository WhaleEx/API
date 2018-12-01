package com.whaleex.api.client.pojo.request;


public class AutoOrderRequest extends RequestBody {
    String amount;
    String price;
    String symbol;
    String type;

    public interface OrderType{

        String sell_limit ="sell-limit";
        String buy_limit ="buy-limit";
        String buy_market ="buy-market";
        String sell_market ="sell-market";
    }


    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
