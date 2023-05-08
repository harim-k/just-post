package com.example.justpost.domain;

import lombok.Getter;

@Getter
public enum ConvertType {

    NAVER_POST("네이버"), COUPANG_POST("쿠팡"), ABLY_POST("에이블리"),
    NAVER_AFTER_POST("네이버"), COUPANG_AFTER_POST("쿠팡"), ABLY_AFTER_POST("에이블리"),
    ;


    private final String storeName;

    ConvertType(String storeName) {
        this.storeName = storeName;
    }
}
