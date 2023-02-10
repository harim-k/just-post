package com.example.justpost.service.afterPost;

import com.example.justpost.domain.ConvertType;
import com.example.justpost.domain.Invoice;
import com.example.justpost.domain.post.PostHandler;
import com.example.justpost.domain.store.afterPost.AfterPostConverter;
import com.example.justpost.domain.store.afterPost.AfterPostConverterFactory;
import com.example.justpost.domain.post.PostHandlerFactory;
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
                                             String afterPostString) {

        AfterPostConverter afterPostConverter = afterPostConverterFactory.get(convertType);
        PostHandler postHandler = postHandlerFactory.get(afterPostString);
        List<Invoice> invoices;
        // extract after post infos
        if (StringUtils.equals(afterPostString, "")) {
            invoices = postHandler.extractInvoices(postFile);
        } else {
            invoices = postHandler.extractInvoices(afterPostString);
        }
        // convert order excel file to after post excel file
        List<List<String>> afterPostValues = afterPostConverter.convertAndSave(orderFile, invoices);

        return afterPostValues;
    }


    public void downloadFile(HttpServletResponse response,
                             ConvertType convertType) throws IOException {
        AfterPostConverter afterPostConverter = afterPostConverterFactory.get(convertType);
        String filePath = afterPostConverter.getAfterPostFilePath();
        FileUtil.downloadFile(response, filePath);

    }
}
