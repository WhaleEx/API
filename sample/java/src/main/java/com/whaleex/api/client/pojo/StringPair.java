package com.whaleex.api.client.pojo;

public class StringPair {
    String first;
    String second;

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public StringPair(String first, String second) {
        this.first = first;
        this.second = second;
    }
}
