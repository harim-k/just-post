package com.example.justpost.domain.post;

import com.example.justpost.domain.Invoice;
import com.example.justpost.domain.PostInfo;
import com.example.justpost.domain.utils.ExcelUtil;
import com.example.justpost.domain.utils.FileUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class CjPostHandler extends PostHandler {
    public static final String POST_FILE_NAME = "cj_대량발송.xlsx";
    public static final String POST_TEMPLATE_FILE_NAME = "cj_대량발송_템플릿.xlsx";
    public static final int SHEET_INDEX = 0;
    public static final int HEADER_ROW_INDEX = 0;

    @Override
    public void saveAsPostFile(List<PostInfo> postValues,
                               String storeName) throws Exception {
        super.saveAsPostFile(postValues, storeName);
    }


    @Override
    public List<Invoice> extractInvoices(String 택배예약현황String) {
        return null;
    }

    @Override
    public String getPostFilePath(String storeName) {
        return FileUtil.POST_FILE_PATH + storeName + "_" + POST_FILE_NAME;
    }

    @Override
    String getPostTemplateFilePath() {
        return FileUtil.POST_TEMPLATE_FILE_PATH + POST_TEMPLATE_FILE_NAME;
    }

    @Override
    Workbook makePostWorkbook(List<PostInfo> postInfos) throws Exception {
        Workbook postWorkbook = new XSSFWorkbook();
        Workbook postTemplateWorkbook = WorkbookFactory.create(
                new FileInputStream(getPostTemplateFilePath()));

        Sheet postSheet = postWorkbook.createSheet();
        Sheet postTemplateSheet = postTemplateWorkbook.getSheetAt(SHEET_INDEX);

        // copy first row from post template
        ExcelUtil.copyRow(postTemplateSheet, postSheet, HEADER_ROW_INDEX);

        // set second ~ last row from postValues
        for (int i = 0; i < postInfos.size(); i++) {
            PostInfo postInfo = postInfos.get(i);

            ExcelUtil.setRow(postSheet,
                             convertToForm(postInfo),
                             HEADER_ROW_INDEX + i + 1);
        }

        postTemplateWorkbook.close();

        return postWorkbook;
    }

    private List<String> convertToForm(PostInfo postInfo) {
        List<String> rowValues = new ArrayList<>();

        rowValues.add(postInfo.getName());
        rowValues.add(postInfo.getAddress());
        rowValues.add(postInfo.getContact1());
        rowValues.add(String.join(" ", postInfo.getProductInfos()));
        rowValues.add(String.valueOf(postInfo.getProductInfos().size()));
        rowValues.add(postInfo.getMessage());

        return rowValues;
    }
}
