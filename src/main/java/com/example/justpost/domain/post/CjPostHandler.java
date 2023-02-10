package com.example.justpost.domain.post;

import com.example.justpost.domain.*;
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
    public void saveAsPostFile(List<Post> postValues,
                               String storeName) throws Exception {
        super.saveAsPostFile(postValues, storeName);
    }


    @Override
    public List<Invoice> extractInvoices(String 택배예약현황String) {
        return null;
    }

    @Override
    public List<Invoice> extractInvoices(MultipartFile postFile) throws Exception {
        List<Invoice> invoices = new ArrayList<>();

        Workbook postWorkbook = WorkbookFactory.create(postFile.getInputStream());

        String[][] postSheet = ExcelUtil.workbookToArray(postWorkbook, SHEET_INDEX, HEADER_ROW_INDEX);

        InvoiceIndexInfo invoiceIndexInfo = getInvoiceIndexInfo(postSheet);

        for (int rowIndex = HEADER_ROW_INDEX + 1; rowIndex < postSheet.length; rowIndex++) {
            String[] postRow = postSheet[rowIndex];

            Invoice invoice = makeInvoice(postRow, invoiceIndexInfo);
            invoices.add(invoice);
        }

        return invoices;
    }

    private Invoice makeInvoice(String[] postRow, InvoiceIndexInfo invoiceIndexInfo) {
        return Invoice.builder()
                .name(postRow[invoiceIndexInfo.getNameColumnIndex()])
                .postcode(postRow[invoiceIndexInfo.getPostcodeColumnIndex()])
                .invoiceNumber(StringUtils.replace(postRow[invoiceIndexInfo.getInvoiceNumberColumnIndex()], "-", ""))
                .build();
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
    Workbook makePostWorkbook(List<Post> posts) throws Exception {
        Workbook postWorkbook = new XSSFWorkbook();
        Workbook postTemplateWorkbook = WorkbookFactory.create(
                new FileInputStream(getPostTemplateFilePath()));

        Sheet postSheet = postWorkbook.createSheet();
        Sheet postTemplateSheet = postTemplateWorkbook.getSheetAt(SHEET_INDEX);

        // copy first row from post template
        ExcelUtil.copyRow(postTemplateSheet, postSheet, HEADER_ROW_INDEX);

        // set second ~ last row from postValues
        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);

            ExcelUtil.setRow(postSheet,
                             convertToForm(post),
                             HEADER_ROW_INDEX + i + 1);
        }

        postTemplateWorkbook.close();

        return postWorkbook;
    }

    private List<String> convertToForm(Post post) {
        List<String> rowValues = new ArrayList<>();

        rowValues.add(post.getName());
        rowValues.add(post.getAddress());
        rowValues.add(post.getContact1());
        rowValues.add(String.join(" ", post.getProductInfos()));
        rowValues.add(String.valueOf(post.getProductInfos().size()));
        rowValues.add(post.getMessage());

        return rowValues;
    }

    InvoiceIndexInfo getInvoiceIndexInfo(String[][] postSheet) {
        String[] headerRow = postSheet[HEADER_ROW_INDEX];

        return InvoiceIndexInfo.builder()
                .nameColumnIndex(getIndex(headerRow, "받는분"))
                .postcodeColumnIndex(getIndex(headerRow, "받는분  우편번호"))
                .addressColumnIndex(getIndex(headerRow, "받는분주소"))
                .invoiceNumberColumnIndex(getIndex(headerRow, "운송장번호"))
                .build();
    }
}
