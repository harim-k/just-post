package com.example.justpost.service.afterPost;

import com.example.justpost.domain.ConvertType;
import com.example.justpost.domain.DownloadType;
import com.example.justpost.domain.Invoice;
import com.example.justpost.domain.afterPost.AfterPostConverter;
import com.example.justpost.domain.afterPost.AfterPostConverterFactory;
import com.example.justpost.domain.handler.PostHandlerFactory;
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
public class AfterPostService {

    private final AfterPostConverterFactory afterPostConverterFactory;
    private final PostHandlerFactory postHandlerFactory;

    @SneakyThrows
    public List<List<String>> convertAndSave(MultipartFile file,
                                             String afterPostString,
                                             ConvertType convertType) {
        AfterPostConverter afterPostConverter = afterPostConverterFactory.get(convertType);

        // extract after post infos
        List<Invoice> invoices = postHandlerFactory.get(afterPostString).extractInvoices(afterPostString);

        // convert order excel file to after post excel file
        List<List<String>> afterPostValues = afterPostConverter.convertAndSave(file, invoices);

        return afterPostValues;
    }


    public void downloadFile(HttpServletResponse response,
                             ConvertType convertType) throws IOException {
        AfterPostConverter afterPostConverter = afterPostConverterFactory.get(convertType);
        String filePath = afterPostConverter.getAfterPostFilePath();
        FileUtil.downloadFile(response, filePath);

    }
}
