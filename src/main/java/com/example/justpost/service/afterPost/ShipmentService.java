package com.example.justpost.service.afterPost;

import com.example.justpost.domain.post.ConvertType;
import com.example.justpost.domain.post.InvoiceMap;
import com.example.justpost.domain.postClient.PostHandler;
import com.example.justpost.domain.postClient.PostHandlerFactory;
import com.example.justpost.domain.store.afterPost.AfterPostConverter;
import com.example.justpost.domain.store.afterPost.AfterPostConverterFactory;
import com.example.justpost.domain.utils.FileUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class ShipmentService {

    private final AfterPostConverterFactory afterPostConverterFactory;
    private final PostHandlerFactory postHandlerFactory;

    @SneakyThrows
    public List<List<String>> convertAndSave(MultipartFile orderFile,
                                             ConvertType convertType,
                                             MultipartFile postFile,
                                             String postString) {

        AfterPostConverter afterPostConverter = afterPostConverterFactory.get(convertType);
        PostHandler postHandler = postHandlerFactory.get(postString);
        InvoiceMap invoiceMap;
        if (StringUtils.equals(postString, "")) {
            invoiceMap = postHandler.getInvoiceMap(postFile);
        } else {
            invoiceMap = postHandler.getInvoiceMap(postString);
        }

        List<List<String>> afterPostValues = afterPostConverter.convertAndSave(orderFile, invoiceMap);

        return afterPostValues;
    }


    public void downloadFile(HttpServletResponse response,
                             ConvertType convertType) throws IOException {
        AfterPostConverter afterPostConverter = afterPostConverterFactory.get(convertType);
        String filePath = afterPostConverter.getAfterPostFilePath();
        FileUtil.downloadFile(response, filePath);
    }
}
