package com.example.justpost.domain.post;

import com.example.justpost.domain.InvoiceNumberMap;
import com.example.justpost.domain.PostColumnIndex;
import com.example.justpost.domain.PostReservation;
import com.example.justpost.domain.utils.ExcelUtil;
import com.example.justpost.domain.utils.FileUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.justpost.domain.utils.StringUtil.getIndex;

@Component
public class CjPostHandler extends PostHandler {
    public static final String POST_FILE_NAME = "cj_대량발송.xlsx";
    public static final String POST_TEMPLATE_FILE_NAME = "cj_대량발송_템플릿.xlsx";
    public static final int SHEET_INDEX = 0;
    public static final int HEADER_ROW_INDEX = 0;


    @Override
    public InvoiceNumberMap getInvoiceNumberMap(String postString) {
        return null;
    }

    @Override
    public InvoiceNumberMap getInvoiceNumberMap(MultipartFile postFile) throws Exception {
        InvoiceNumberMap invoiceNumberMap = new InvoiceNumberMap();

        Workbook postWorkbook = WorkbookFactory.create(postFile.getInputStream());
        String[][] postSheet = ExcelUtil.workbookToArray(postWorkbook, SHEET_INDEX, HEADER_ROW_INDEX);

        PostColumnIndex postColumnIndex = getPostColumnIndex(postSheet);

        for (int rowIndex = HEADER_ROW_INDEX + 1; rowIndex < postSheet.length; rowIndex++) {
            String[] postRow = postSheet[rowIndex];

            String name = postRow[postColumnIndex.getNameColumnIndex()];
            String postcode = postRow[postColumnIndex.getPostcodeColumnIndex()];
            String invoiceNumber = StringUtils.replace(postRow[postColumnIndex.getInvoiceNumberColumnIndex()], "-", "");

            invoiceNumberMap.put(name, postcode, invoiceNumber);
        }

        return invoiceNumberMap;
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
    Workbook convertToWorkbook(List<PostReservation> postReservations) throws Exception {
        Workbook postWorkbook = new XSSFWorkbook();
        Workbook postTemplateWorkbook = WorkbookFactory.create(
                new FileInputStream(getPostTemplateFilePath()));

        Sheet postSheet = postWorkbook.createSheet();
        Sheet postTemplateSheet = postTemplateWorkbook.getSheetAt(SHEET_INDEX);

        // copy first row from post template
        ExcelUtil.copyRow(postTemplateSheet, postSheet, HEADER_ROW_INDEX);

        // set second ~ last row from postValues
        for (int i = 0; i < postReservations.size(); i++) {
            PostReservation postReservation = postReservations.get(i);

            ExcelUtil.setRow(postSheet,
                             convertToForm(postReservation),
                             HEADER_ROW_INDEX + i + 1);
        }

        postTemplateWorkbook.close();

        return postWorkbook;
    }

    private List<String> convertToForm(PostReservation postReservation) {
        List<String> rowValues = new ArrayList<>();

        rowValues.add(postReservation.getName());
        rowValues.add(postReservation.getAddress());
        rowValues.add(postReservation.getContact1());
        rowValues.add(String.join(" ", postReservation.getProducts()));
        rowValues.add(String.valueOf(postReservation.getProducts().size()));
        rowValues.add(postReservation.getMessage());

        return rowValues;
    }

    PostColumnIndex getPostColumnIndex(String[][] postSheet) {
        String[] headerRow = postSheet[HEADER_ROW_INDEX];

        return PostColumnIndex.builder()
                .nameColumnIndex(getIndex(headerRow, "받는분"))
                .postcodeColumnIndex(getIndex(headerRow, "받는분  우편번호"))
                .invoiceNumberColumnIndex(getIndex(headerRow, "운송장번호"))
                .build();
    }
}
