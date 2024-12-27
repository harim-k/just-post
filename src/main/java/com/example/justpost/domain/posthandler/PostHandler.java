package com.example.justpost.domain.posthandler;

import com.example.justpost.domain.post.InvoiceMap;
import com.example.justpost.domain.post.Posts;
import com.example.justpost.domain.utils.ExcelUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

public abstract class PostHandler {

    public void save(Posts posts, String storeName) throws Exception {
        Workbook postWorkbook = convertToWorkbook(posts);

        // save
        ExcelUtil.save(postWorkbook, getPostFilePath(storeName));

        // close
        postWorkbook.close();
    }

    public abstract InvoiceMap getInvoiceMap(String postString);

    public abstract InvoiceMap getInvoiceMap(MultipartFile postFile) throws Exception;

    public abstract String getPostFilePath(String storeName);

    abstract String getPostTemplateFilePath();

    abstract Workbook convertToWorkbook(Posts postValues) throws Exception;
}
