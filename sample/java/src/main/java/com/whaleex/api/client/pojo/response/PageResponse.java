package com.whaleex.api.client.pojo.response;

import java.util.List;

public class PageResponse<T> {
    List<T> content;
    Integer totalElements;

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public Integer getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Integer totalElements) {
        this.totalElements = totalElements;
    }

    @Override
    public String toString() {
        return "PageResponse{" +
                "content=" + content +
                ", totalElements=" + totalElements +
                '}';
    }
}
