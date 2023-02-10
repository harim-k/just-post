package com.example.justpost.service.post;

import com.example.justpost.domain.ConvertType;
import com.example.justpost.domain.DownloadType;
import com.example.justpost.domain.PostReservation;
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
    public List<PostReservation> convertAndSave(MultipartFile orderFile, ConvertType convertType) {
        PostConverter postConverter = postConverterFactory.get(convertType);
        List<PostReservation> postReservations = postConverter.convert(orderFile);
        save(postReservations, convertType.getStoreName());

        return postReservations;
    }

    private void save(List<PostReservation> postReservations, String storeName) throws Exception {
        cjPostHandler.save(postReservations, storeName);
        gsPostHandler.save(postReservations, storeName);
        cuPostHandler.save(postReservations, storeName);
    }

    public void downloadFile(HttpServletResponse response,
                             DownloadType downloadType,
                             ConvertType convertType) throws IOException {
        PostHandler postHandler = postHandlerFactory.get(downloadType);
        String filePath = postHandler.getPostFilePath(convertType.getStoreName());
        FileUtil.downloadFile(response, filePath);
    }
}
