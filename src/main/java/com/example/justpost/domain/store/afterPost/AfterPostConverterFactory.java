package com.example.justpost.domain.store.afterPost;

import com.example.justpost.domain.post.ConvertType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AfterPostConverterFactory {
    private final NaverAfterPostConverter naverAfterPostConverter;
    private final CoupangAfterPostConverter coupangAfterPostConverter;
    private final AblyAfterPostConverter ablyAfterPostConverter;


    public AfterPostConverter get(ConvertType type) {
        switch (type) {
            case NAVER_AFTER_POST:
                return naverAfterPostConverter;
            case COUPANG_AFTER_POST:
                return coupangAfterPostConverter;
            case ABLY_AFTER_POST:
                return ablyAfterPostConverter;
            default:
                return null;
        }
    }

}
