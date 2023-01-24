package com.example.justpost.domain.afterPost;

import com.example.justpost.domain.Invoice;
import com.example.justpost.domain.utils.ExcelUtil;
import com.example.justpost.domain.utils.FileUtil;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class AblyAfterPostConverter extends AfterPostConverter {
    public static final int SHEET_INDEX = 1;
    public static final int HEADER_ROW_INDEX = 0;
    public static final String AFTER_POST_FILE_NAME = "에이블리_발송처리.xlsx";


    @Override
    public List<List<String>> convertAndSave(MultipartFile file,
                                             List<Invoice> invoices) throws Exception {
        List<List<String>> afterPostValues = new ArrayList<>();
        Map<Pair<String, String>, String> invoiceMap = makeInvoiceMap(invoices);

        Workbook orderWorkbook = WorkbookFactory.create(file.getInputStream());
        Sheet orderSheet = orderWorkbook.getSheetAt(SHEET_INDEX);
        Row orderHeaderRow = orderSheet.getRow(HEADER_ROW_INDEX);

        int 수취인명ColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "수취인명");
        int 우편번호ColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "우편번호");

        // 에이블리는 주문 엑셀 파일에 운송장번호를 넣어 업로드하는 구조
        for (int rowIndex = HEADER_ROW_INDEX + 1; rowIndex <= orderSheet.getLastRowNum(); rowIndex++) {
            Row orderRow = orderSheet.getRow(rowIndex);

            String 수취인명 = ExcelUtil.getValue(orderRow.getCell(수취인명ColumnIndex));
            String 우편번호 = ExcelUtil.getValue(orderRow.getCell(우편번호ColumnIndex));
            String 운송장번호 = invoiceMap.getOrDefault(new Pair<>(수취인명, 우편번호), null);

            // 5번째 값이 운송장번호
            orderRow.createCell(5).setCellValue(운송장번호);

            afterPostValues.add(ExcelUtil.getValues(orderRow));
        }

        // save after post workbook
        ExcelUtil.save(orderWorkbook, getAfterPostFilePath());

        // close workbook
        orderWorkbook.close();

        return afterPostValues;
    }

    @Override
    public String getAfterPostFilePath() {
        return FileUtil.AFTER_POST_FILE_PATH + AFTER_POST_FILE_NAME;
    }
}
