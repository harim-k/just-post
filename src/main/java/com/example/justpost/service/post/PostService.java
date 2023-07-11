package com.example.justpost.service.post;

import com.example.justpost.domain.ConvertType;
import com.example.justpost.domain.DownloadType;
import com.example.justpost.domain.Posts;
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

@Service
@AllArgsConstructor
public class PostService {
    private final PostConverterFactory postConverterFactory;
    private final PostHandlerFactory postHandlerFactory;
    private final CjPostHandler cjPostHandler;
    private final GsPostHandler gsPostHandler;
    private final CuPostHandler cuPostHandler;

    @SneakyThrows
    public Posts convertAndSave(MultipartFile orderFile, ConvertType convertType) {
        PostConverter postConverter = postConverterFactory.get(convertType);
        Posts posts = Posts.create(orderFile, postConverter);

        save(posts, convertType.getStoreName());

        return posts;
    }

    private void save(Posts posts, String storeName) throws Exception {
        cjPostHandler.save(posts, storeName);
        gsPostHandler.save(posts, storeName);
        cuPostHandler.save(posts, storeName);
    }

    public void downloadFile(HttpServletResponse response,
                             DownloadType downloadType,
                             ConvertType convertType) throws IOException {
        PostHandler postHandler = postHandlerFactory.get(downloadType);
        String filePath = postHandler.getPostFilePath(convertType.getStoreName());
        FileUtil.downloadFile(response, filePath);
    }
}
