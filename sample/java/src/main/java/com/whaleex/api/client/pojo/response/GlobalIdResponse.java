package com.whaleex.api.client.pojo.response;

import java.util.List;

public class GlobalIdResponse {

    private String remark;
    List<String> list;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "GlobalIdResponse{" +
                "remark='" + remark + '\'' +
                ", list=" + list +
                '}';
    }
}
