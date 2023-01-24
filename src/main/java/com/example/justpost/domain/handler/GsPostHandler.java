package com.example.justpost.domain.handler;

import com.example.justpost.domain.Invoice;
import com.example.justpost.domain.utils.ExcelUtil;
import com.example.justpost.domain.utils.FileUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class GsPostHandler extends PostHandler {
    public static final String POST_FILE_NAME = "gs_대량발송.xls";
    public static final String POST_TEMPLATE_FILE_NAME = "gs_대량발송_템플릿.xls";
    public static final int SHEET_INDEX = 0;
    public static final int HEADER_ROW_INDEX = 0;

    @Override
    public void saveAsPostFile(List<List<String>> postValues,
                               String storeName) throws IOException {
        super.saveAsPostFile(postValues, storeName);
    }


    @Override
    public List<Invoice> extractInvoices(String 택배예약현황String) {
        List<Invoice> invoices = new ArrayList<>();

        String[] strings = 택배예약현황String.replace("\r\n", "")
                .replace("\n", "")
                .split("수신정보")[1]
                .split("선불");

        strings = Arrays.copyOfRange(strings, 0, strings.length - 1);

        final String delimeter = strings[0].contains("반품") ? "반품" : "Address";

        for (String string : strings) {
            String name = string.split(delimeter)[0];
            String postcode = string.split("\\[")[1]
                    .split("]")[0];
            String invoiceNumber = string.split("운송장번호")[1]
                    .split("Comment")[0];

            invoices.add(new Invoice(name, postcode, invoiceNumber));
        }

        return invoices;
    }

    @Override
    public String getPostFilePath(String storeName) {
        return FileUtil.POST_FILE_PATH + storeName + "_" + POST_FILE_NAME;
    }

    @Override
    String getPostTemplateFilePath() {
        return FileUtil.TEMPLATE_FILE_PATH + POST_TEMPLATE_FILE_NAME;
    }


    Workbook makePostWorkbook(List<List<String>> postValues, Workbook postTemplateWorkbook) {
        Workbook postWorkbook = new HSSFWorkbook();

        Sheet postSheet = postWorkbook.createSheet();
        Sheet postTemplateSheet = postTemplateWorkbook.getSheetAt(SHEET_INDEX);

        // copy first row from post template
        ExcelUtil.copyRow(postTemplateSheet, postSheet, HEADER_ROW_INDEX);

        // set second ~ last row from postValues
        ExcelUtil.setValues(postSheet, postValues, HEADER_ROW_INDEX + 1);

        return postWorkbook;
    }
}
