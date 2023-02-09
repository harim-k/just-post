package com.example.justpost.service.post;

import com.example.justpost.domain.ConvertType;
import com.example.justpost.domain.DownloadType;
import com.example.justpost.domain.PostInfo;
import com.example.justpost.domain.post.*;
import com.example.justpost.domain.store.post.PostConverter;
import com.example.justpost.domain.store.post.PostConverterFactory;
import com.example.justpost.domain.utils.FileUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class PostService {
    private final PostConverterFactory postConverterFactory;
    private final PostHandlerFactory postHandlerFactory;
    private final CjPostHandler cjPostHandler;
    private final GsPostHandler gsPostHandler;
    private final CuPostHandler cuPostHandler;

    @SneakyThrows
    public List<PostInfo> convertAndSave(MultipartFile file, ConvertType convertType) {
        PostConverter postConverter = postConverterFactory.get(convertType);

        // convert order excel file to post excel file
        List<PostInfo> postInfos = postConverter.convert(file);

        // save as post excel file
        saveAsPostFile(postInfos, convertType.getStoreName());

        return postInfos;
    }

    private void saveAsPostFile(List<PostInfo> postInfos, String storeName) throws Exception {
        cjPostHandler.saveAsPostFile(postInfos, storeName);
        gsPostHandler.saveAsPostFile(postInfos, storeName);
        cuPostHandler.saveAsPostFile(postInfos, storeName);
    }

    public void downloadFile(HttpServletResponse response,
                             DownloadType downloadType,
                             ConvertType convertType) throws IOException {
        PostHandler postHandler = postHandlerFactory.get(downloadType);
        String filePath = postHandler.getPostFilePath(convertType.getStoreName());
        FileUtil.downloadFile(response, filePath);
    }
}
