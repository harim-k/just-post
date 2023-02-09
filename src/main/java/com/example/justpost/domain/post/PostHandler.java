package com.example.justpost.domain.post;

import com.example.justpost.domain.Invoice;
import com.example.justpost.domain.Post;
import com.example.justpost.domain.utils.ExcelUtil;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

public abstract class PostHandler {

    public void saveAsPostFile(List<Post> posts,
                               String storeName) throws Exception {
        Workbook postWorkbook = makePostWorkbook(posts);

        // save
        ExcelUtil.save(postWorkbook, getPostFilePath(storeName));

        // close
        postWorkbook.close();
    }

    public abstract List<Invoice> extractInvoices(String 택배예약현황String);

    abstract Workbook makePostWorkbook(List<Post> postValues) throws Exception;

    public abstract String getPostFilePath(String storeName);

    abstract String getPostTemplateFilePath();
}
