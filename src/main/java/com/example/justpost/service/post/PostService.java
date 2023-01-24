package com.example.justpost.service.post;

import com.example.justpost.domain.ConvertType;
import com.example.justpost.domain.DownloadType;
import com.example.justpost.domain.handler.CuPostHandler;
import com.example.justpost.domain.handler.GsPostHandler;
import com.example.justpost.domain.handler.PostHandler;
import com.example.justpost.domain.handler.PostHandlerFactory;
import com.example.justpost.domain.post.converter.PostConverter;
import com.example.justpost.domain.post.converter.PostConverterFactory;
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
    private final GsPostHandler gsPostHandler;
    private final CuPostHandler cuPostHandler;

    @SneakyThrows
    public List<List<String>> convertAndSave(MultipartFile file, ConvertType convertType) {
        PostConverter postConverter = postConverterFactory.get(convertType);

        // convert order excel file to post excel file
        List<List<String>> postValues = postConverter.convert(file);

        // save as post excel file
        saveAsPostFile(postValues, convertType.getStoreName());

        return postValues;
    }

    private void saveAsPostFile(List<List<String>> postValues, String storeName) throws IOException {
        gsPostHandler.saveAsPostFile(postValues, storeName);
        cuPostHandler.saveAsPostFile(postValues, storeName);
    }

    public void downloadFile(HttpServletResponse response,
                             DownloadType downloadType,
                             ConvertType convertType) throws IOException {
        PostHandler postHandler = postHandlerFactory.get(downloadType);
        String filePath = postHandler.getPostFilePath(convertType.getStoreName());
        FileUtil.downloadFile(response, filePath);
    }
}
