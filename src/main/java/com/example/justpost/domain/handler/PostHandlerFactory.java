package com.example.justpost.domain.handler;

import com.example.justpost.domain.DownloadType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostHandlerFactory {
    private final GsPostHandler gsPostHandler;
    private final CuPostHandler cuPostHandler;


    public PostHandler get(String afterPostString) {
        if (afterPostString.contains("CUPOST")) {
            return cuPostHandler;
        } else {
            return gsPostHandler;
        }
    }

    public PostHandler get(DownloadType downloadType) {
        if (downloadType == DownloadType.CU_POST) {
            return cuPostHandler;
        } else if (downloadType == DownloadType.GS_POST) {
            return gsPostHandler;
        } else {
            return null;
        }
    }

}
