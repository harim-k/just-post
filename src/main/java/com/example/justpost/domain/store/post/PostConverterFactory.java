package com.example.justpost.domain.store.post;

import com.example.justpost.domain.ConvertType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostConverterFactory {
    private final NaverPostConverter naverPostConverter;
    private final CoupangPostConverter coupangPostConverter;
    private final AblyPostConverter ablyPostConverter;


    public PostConverter get(ConvertType type) {
        switch (type) {
            case NAVER_POST:
                return naverPostConverter;
            case COUPANG_POST:
                return coupangPostConverter;
            case ABLY_POST:
                return ablyPostConverter;
            default:
                return null;
        }
    }

}
