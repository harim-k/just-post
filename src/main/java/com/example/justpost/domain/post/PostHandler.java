package com.example.justpost.domain.post;

import com.example.justpost.domain.Invoice;
import com.example.justpost.domain.PostInfo;
import com.example.justpost.domain.utils.ExcelUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public abstract class PostHandler {

    public void saveAsPostFile(List<PostInfo> postInfos,
                               String storeName) throws Exception {
        Workbook postWorkbook = makePostWorkbook(postInfos);

        // save
        ExcelUtil.save(postWorkbook, getPostFilePath(storeName));

        // close
        postWorkbook.close();
    }

    public abstract List<Invoice> extractInvoices(String 택배예약현황String);

    abstract Workbook makePostWorkbook(List<PostInfo> postValues) throws Exception;

    public abstract String getPostFilePath(String storeName);

    abstract String getPostTemplateFilePath();
}
