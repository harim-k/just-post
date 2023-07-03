package com.example.justpost.domain.store.post;

import com.example.justpost.domain.OrderColumnIndex;
import com.example.justpost.domain.Posts;
import com.example.justpost.domain.utils.ExcelUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;

public abstract class PostConverter {

    public Posts convert(MultipartFile orderFile) throws Exception {
        String[][] orderSheet = getOrderSheet(orderFile);
        OrderColumnIndex orderColumnIndex = getOrderColumnIndex(orderSheet);
        return Posts.create(orderSheet, orderColumnIndex, this);
    }


    String[][] getOrderSheet(MultipartFile file) throws Exception {
        Workbook orderWorkbook = WorkbookFactory.create(file.getInputStream());
        String[][] orderSheet = ExcelUtil.workbookToArray(
                orderWorkbook,
                getSheetIndex(),
                getHeaderRowIndex());

        // close workbook
        orderWorkbook.close();
        return orderSheet;
    }


    abstract public String getProduct(String product,
                                      String option,
                                      String count);

    abstract OrderColumnIndex getOrderColumnIndex(String[][] orderSheet);

    abstract int getSheetIndex();

    public abstract int getHeaderRowIndex();

}
