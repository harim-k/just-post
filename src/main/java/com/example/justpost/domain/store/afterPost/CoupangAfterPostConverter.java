package com.example.justpost.domain.store.afterPost;

import com.example.justpost.domain.InvoiceMap;
import com.example.justpost.domain.utils.ExcelUtil;
import com.example.justpost.domain.utils.FileUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CoupangAfterPostConverter extends AfterPostConverter {
    public static final int SHEET_INDEX = 0;
    public static final int HEADER_ROW_INDEX = 0;
    public static final String AFTER_POST_FILE_NAME = "coupang_after_post.xlsx";
    public static final String AFTER_POST_TEMPLATE_FILE_NAME = "coupang_after_post_template.xlsx";


    @Override
    public List<List<String>> convertAndSave(MultipartFile file,
                                             InvoiceMap invoiceMap) throws Exception {
        List<List<String>> afterPostValues = convert(file, invoiceMap);
        saveAsAfterPostFile(afterPostValues);

        return afterPostValues;
    }

    @Override
    public String getAfterPostFilePath() {
        return FileUtil.AFTER_POST_FILE_PATH + AFTER_POST_FILE_NAME;
    }

    private String getAfterPostTemplateFilePath() {
        return FileUtil.AFTER_POST_TEMPLATE_FILE_PATH + AFTER_POST_TEMPLATE_FILE_NAME;
    }

    public List<List<String>> convert(MultipartFile file,
                                      InvoiceMap invoiceMap) throws Exception {
        List<List<String>> afterPostValues = new ArrayList<>();

        Workbook orderWorkbook = WorkbookFactory.create(file.getInputStream());
        Sheet orderSheet = orderWorkbook.getSheetAt(SHEET_INDEX);
        Row orderHeaderRow = orderSheet.getRow(HEADER_ROW_INDEX);

        int 수취인명ColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "수취인이름");
        int 우편번호ColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "우편번호");

        int 번호ColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "번호");
        int 묶음배송번호ColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "묶음배송번호");
        int 주문번호ColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "주문번호");
        int 옵션IDColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "옵션ID");

        final String 택배사 = "CJ 대한통운";

        for (int rowIndex = HEADER_ROW_INDEX + 1; rowIndex <= orderSheet.getLastRowNum(); rowIndex++) {
            Row orderRow = orderSheet.getRow(rowIndex);

            String 수취인명 = ExcelUtil.getValue(orderRow.getCell(수취인명ColumnIndex));
            String 우편번호 = ExcelUtil.getValue(orderRow.getCell(우편번호ColumnIndex));

            String 번호 = ExcelUtil.getValue(orderRow.getCell(번호ColumnIndex));
            String 묶음배송번호 = ExcelUtil.getValue(orderRow.getCell(묶음배송번호ColumnIndex));
            String 주문번호 = ExcelUtil.getValue(orderRow.getCell(주문번호ColumnIndex));

            String 운송장번호 = invoiceMap.get(우편번호);
            String 옵션ID = ExcelUtil.getValue(orderRow.getCell(옵션IDColumnIndex));

            if (운송장번호 == null) {
                continue;
            }

            afterPostValues.add(new ArrayList<>(
                    Arrays.asList(번호, 묶음배송번호, 주문번호, 택배사, 운송장번호, "N",
                                  null, null, null, null, null, null, null, null, 옵션ID)));
        }

        // close workbook
        orderWorkbook.close();

        return afterPostValues;
    }

    private void saveAsAfterPostFile(List<List<String>> afterPostValues) throws Exception {
        Workbook postWorkbook = new XSSFWorkbook();
        Workbook postTemplateWorkbook = WorkbookFactory.create(
                new FileInputStream(getAfterPostTemplateFilePath()));
        // xlsx 파일은 XSSFWorkbook로, xls 파일은 HSSFWorkbook로 다뤄야함
        // 그래서 WorkbookFactory 사용

        Sheet postSheet = postWorkbook.createSheet();
        Sheet postTemplateSheet = postTemplateWorkbook.getSheetAt(0);

        // copy first row from post template
        ExcelUtil.copyRow(postTemplateSheet, postSheet, 0);

        // set second ~ last row from postValues
        ExcelUtil.setValues(postSheet, afterPostValues, 1);

        // save
        ExcelUtil.save(postWorkbook, getAfterPostFilePath());

        // close
        postWorkbook.close();
        postTemplateWorkbook.close();
    }

}