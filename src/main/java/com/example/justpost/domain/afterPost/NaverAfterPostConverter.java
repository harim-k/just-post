package com.example.justpost.domain.afterPost;

import com.example.justpost.domain.Invoice;
import com.example.justpost.domain.utils.ExcelUtil;
import com.example.justpost.domain.utils.FileUtil;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class NaverAfterPostConverter extends AfterPostConverter {
    public static final int SHEET_INDEX = 0;
    public static final int HEADER_ROW_INDEX = 1;
    public static final String AFTER_POST_FILE_NAME = "네이버_발송처리.xls";
    public static final String AFTER_POST_TEMPLATE_FILE_NAME = "네이버_발송처리_템플릿.xlsx";


    @Override
    public List<List<String>> convertAndSave(MultipartFile file,
                                             List<Invoice> invoices) throws Exception {
        List<List<String>> afterPostValues = convert(file, invoices);
        saveAsAfterPostFile(afterPostValues);

        return afterPostValues;
    }

    @Override
    public String getAfterPostFilePath() {
        return FileUtil.AFTER_POST_FILE_PATH + AFTER_POST_FILE_NAME;
    }

    private String getAfterPostTemplateFilePath() {
        return FileUtil.TEMPLATE_FILE_PATH + AFTER_POST_TEMPLATE_FILE_NAME;
    }

    public List<List<String>> convert(MultipartFile file,
                                      List<Invoice> invoices) throws Exception {
        List<List<String>> afterPostValues = new ArrayList<>();
        Map<Pair<String, String>, String> invoiceMap = makeInvoiceMap(invoices);

        Workbook orderWorkbook = decryptExcelFile(file);
        Sheet orderSheet = orderWorkbook.getSheetAt(SHEET_INDEX);
        Row orderHeaderRow = orderSheet.getRow(HEADER_ROW_INDEX);

        int 수취인명ColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "수취인명");
        int 우편번호ColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "우편번호");
        int 상품주문번호ColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "상품주문번호");

        final String 배송방법 = "택배,등기,소포";
        final String 택배사 = "CJ 대한통운";

        for (int rowIndex = HEADER_ROW_INDEX + 1; rowIndex <= orderSheet.getLastRowNum(); rowIndex++) {
            Row orderRow = orderSheet.getRow(rowIndex);

            String 수취인명 = ExcelUtil.getValue(orderRow.getCell(수취인명ColumnIndex));
            String 우편번호 = ExcelUtil.getValue(orderRow.getCell(우편번호ColumnIndex));
            String 상품주문번호 = ExcelUtil.getValue(orderRow.getCell(상품주문번호ColumnIndex));
            String 운송장번호 = invoiceMap.getOrDefault(new Pair<>(수취인명, 우편번호), null);

            if (운송장번호 == null) {
                continue;
            }

            afterPostValues.add(new ArrayList<>(Arrays.asList(상품주문번호, 배송방법, 택배사, 운송장번호)));
        }

        // close workbook
        orderWorkbook.close();

        return afterPostValues;
    }

    private void saveAsAfterPostFile(List<List<String>> afterPostValues) throws Exception {
        Workbook postWorkbook = new HSSFWorkbook();
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

    private Workbook decryptExcelFile(MultipartFile file) throws Exception {
        POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
        EncryptionInfo info = new EncryptionInfo(fs);
        Decryptor decryptor = Decryptor.getInstance(info);

        if (!decryptor.verifyPassword("1111")) {
            throw new Exception("Incorrect password");
        }

        Workbook orderWorkbook = WorkbookFactory.create(decryptor.getDataStream(fs));
        return orderWorkbook;
    }
}
