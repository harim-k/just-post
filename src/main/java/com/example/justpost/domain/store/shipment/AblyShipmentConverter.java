package com.example.justpost.domain.store.shipment;

import com.example.justpost.domain.post.InvoiceMap;
import com.example.justpost.domain.post.PostColumnIndex;
import com.example.justpost.domain.utils.ExcelUtil;
import com.example.justpost.domain.utils.FileUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.example.justpost.domain.utils.StringUtil.getIndex;

@Component
public class AblyShipmentConverter extends ShipmentConverter {
    public static final int SHEET_INDEX = 1;
    public static final int HEADER_ROW_INDEX = 0;
    public static final String AFTER_POST_FILE_NAME = "ably_after_post.xlsx";

    @Override
    public List<List<String>> convertAndSave(MultipartFile file,
                                             InvoiceMap invoiceMap) throws Exception {
        List<List<String>> shipmentValues = new ArrayList<>();

        Workbook orderWorkbook = WorkbookFactory.create(file.getInputStream());
        Sheet orderSheet = orderWorkbook.getSheetAt(SHEET_INDEX);
        String[][] orderSheet2 = ExcelUtil.workbookToArray(
                orderWorkbook, SHEET_INDEX, HEADER_ROW_INDEX);

        PostColumnIndex postColumnIndex = getPostColumnIndex(orderSheet2);

        for (int rowIndex = HEADER_ROW_INDEX + 1; rowIndex < orderSheet2.length; rowIndex++) {
            String[] orderRow = orderSheet2[rowIndex];

            String name = orderRow[postColumnIndex.getNameColumnIndex()];
            String postcode = orderRow[postColumnIndex.getPostcodeColumnIndex()];
            String invoiceNumber = invoiceMap.get(postcode);

            // 에이블리는 주문 엑셀 파일에 운송장번호를 넣어 업로드하는 구조
            orderSheet.getRow(rowIndex).createCell(5).setCellValue(invoiceNumber);
            shipmentValues.add(ExcelUtil.getValues(orderSheet.getRow(rowIndex)));
        }

        // save after post workbook
        ExcelUtil.save(orderWorkbook, getShipmentFilePath());

        // close workbook
        orderWorkbook.close();

        return shipmentValues;
    }

    @Override
    public String getShipmentFilePath() {
        return FileUtil.AFTER_POST_FILE_PATH + AFTER_POST_FILE_NAME;
    }

    PostColumnIndex getPostColumnIndex(String[][] postSheet) {
        String[] headerRow = postSheet[HEADER_ROW_INDEX];
        return PostColumnIndex.builder()
                .nameColumnIndex(getIndex(headerRow, "수취인명"))
                .postcodeColumnIndex(getIndex(headerRow, "우편번호"))
                .invoiceNumberColumnIndex(getIndex(headerRow, "송장번호"))
                .build();
    }
}
