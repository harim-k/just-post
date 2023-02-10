package com.example.justpost.domain.post;

import com.example.justpost.domain.DownloadType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

@Component
@RequiredArgsConstructor
public class PostHandlerFactory {
    private final CjPostHandler cjPostHandler;
    private final GsPostHandler gsPostHandler;
    private final CuPostHandler cuPostHandler;


    public PostHandler get(String afterPostString) {
        if (StringUtils.equals(afterPostString, "")) {
            return cjPostHandler;
        } else if (afterPostString.contains("CUPOST")) {
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
