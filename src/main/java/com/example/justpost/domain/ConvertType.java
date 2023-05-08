package com.example.justpost.domain;

import lombok.Getter;

@Getter
public enum ConvertType {

    NAVER_POST("naver"), COUPANG_POST("coupang"), ABLY_POST("ably"),
    NAVER_AFTER_POST("naver"), COUPANG_AFTER_POST("coupang"), ABLY_AFTER_POST("ably"),
    ;


    private final String storeName;

    ConvertType(String storeName) {
        this.storeName = storeName;
    }
}
