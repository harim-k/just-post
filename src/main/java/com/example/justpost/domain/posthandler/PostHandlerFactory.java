package com.example.justpost.domain.posthandler;

import com.example.justpost.domain.post.DownloadType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PostHandlerFactory {
    private final CjPostHandler cjPostHandler;
    private final GsPostHandler gsPostHandler;
    private final CuPostHandler cuPostHandler;


    public PostHandler get(String shipmentString) {
        if (StringUtils.equals(shipmentString, "")) {
            return cjPostHandler;
        } else if (shipmentString.contains("CUPOST")) {
            return cuPostHandler;
        } else {
            return gsPostHandler;
        }
    }

    public PostHandler get(DownloadType downloadType) {
        if (downloadType == DownloadType.CJ_POST) {
            return cjPostHandler;
        } else if (downloadType == DownloadType.GS_POST) {
            return gsPostHandler;
        } else if (downloadType == DownloadType.CU_POST) {
            return cuPostHandler;
        } else {
            return null;
        }
    }

}
