package com.example.justpost.service.afterPost;

import com.example.justpost.domain.ConvertType;
import com.example.justpost.domain.InvoiceNumberMap;
import com.example.justpost.domain.post.PostHandler;
import com.example.justpost.domain.post.PostHandlerFactory;
import com.example.justpost.domain.store.afterPost.AfterPostConverter;
import com.example.justpost.domain.store.afterPost.AfterPostConverterFactory;
import com.example.justpost.domain.utils.FileUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class AfterPostService {

    private final AfterPostConverterFactory afterPostConverterFactory;
    private final PostHandlerFactory postHandlerFactory;

    @SneakyThrows
    public List<List<String>> convertAndSave(MultipartFile orderFile,
                                             ConvertType convertType,
                                             MultipartFile postFile,
                                             String postString) {

        AfterPostConverter afterPostConverter = afterPostConverterFactory.get(convertType);
        PostHandler postHandler = postHandlerFactory.get(postString);
        InvoiceNumberMap invoiceNumberMap;
        if (StringUtils.equals(postString, "")) {
            invoiceNumberMap = postHandler.getInvoiceNumberMap(postFile);
        } else {
            invoiceNumberMap = postHandler.getInvoiceNumberMap(postString);
        }

        //TODO postMap을 넘기도록 리팩터링
        List<List<String>> afterPostValues = afterPostConverter.convertAndSave(orderFile, invoiceNumberMap);

        return afterPostValues;
    }


    public void downloadFile(HttpServletResponse response,
                             ConvertType convertType) throws IOException {
        AfterPostConverter afterPostConverter = afterPostConverterFactory.get(convertType);
        String filePath = afterPostConverter.getAfterPostFilePath();
        FileUtil.downloadFile(response, filePath);

    }
}
