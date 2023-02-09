package com.example.justpost.domain.post;

import com.example.justpost.domain.Invoice;
import com.example.justpost.domain.utils.ExcelUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public abstract class PostHandler {

    public void saveAsPostFile(List<List<String>> postValues,
                               String storeName) throws IOException {
        Workbook postTemplateWorkbook = WorkbookFactory.create(
                new FileInputStream(getPostTemplateFilePath()));
        // xlsx 파일은 XSSFWorkbook로, xls 파일은 HSSFWorkbook로 다뤄야함
        // 그래서 WorkbookFactory 사용

        Workbook postWorkbook = makePostWorkbook(postValues, postTemplateWorkbook);

        // save
        ExcelUtil.save(postWorkbook, getPostFilePath(storeName));

        // close
        postWorkbook.close();
        postTemplateWorkbook.close();
    }

    public abstract List<Invoice> extractInvoices(String 택배예약현황String);

    abstract Workbook makePostWorkbook(List<List<String>> postValues, Workbook postTemplateWorkbook);

    public abstract String getPostFilePath(String storeName);

    abstract String getPostTemplateFilePath();
}
