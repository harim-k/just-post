package com.example.justpost.domain.store.shipment;

import com.example.justpost.domain.post.ConvertType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShipmentConverterFactory {
    private final NaverShipmentConverter naverShipmentConverter;
    private final CoupangShipmentConverter coupangShipmentConverter;
    private final AblyShipmentConverter ablyShipmentConverter;


    public ShipmentConverter get(ConvertType type) {
        switch (type) {
            case NAVER_AFTER_POST:
                return naverShipmentConverter;
            case COUPANG_AFTER_POST:
                return coupangShipmentConverter;
            case ABLY_AFTER_POST:
                return ablyShipmentConverter;
            default:
                return null;
        }
    }

}
