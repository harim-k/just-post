package com.example.justpost.domain.post;

import com.example.justpost.domain.InvoiceMap;
import com.example.justpost.domain.PostColumnIndex;
import com.example.justpost.domain.Post;
import com.example.justpost.domain.Posts;
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
    public static final String POST_FILE_NAME = "cj_post.xlsx";
    public static final String POST_TEMPLATE_FILE_NAME = "cj_post_template.xlsx";
    public static final int SHEET_INDEX = 0;
    public static final int HEADER_ROW_INDEX = 0;


    @Override
    public InvoiceMap getInvoiceMap(String postString) {
        return null;
    }

    @Override
    public InvoiceMap getInvoiceMap(MultipartFile postFile) throws Exception {
        InvoiceMap invoiceMap = new InvoiceMap();

        Workbook postWorkbook = WorkbookFactory.create(postFile.getInputStream());
        String[][] postSheet = ExcelUtil.workbookToArray(postWorkbook, SHEET_INDEX, HEADER_ROW_INDEX);

        PostColumnIndex postColumnIndex = getPostColumnIndex(postSheet);

        for (int rowIndex = HEADER_ROW_INDEX + 1; rowIndex < postSheet.length; rowIndex++) {
            String[] postRow = postSheet[rowIndex];

            String name = postRow[postColumnIndex.getNameColumnIndex()];
            String postcode = postRow[postColumnIndex.getPostcodeColumnIndex()];
            String invoiceNumber = StringUtils.replace(postRow[postColumnIndex.getInvoiceNumberColumnIndex()], "-", "");

            invoiceMap.put(postcode, invoiceNumber);
        }

        return invoiceMap;
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
    Workbook convertToWorkbook(Posts posts) throws Exception {
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
        rowValues.add(post.getContact1());
        rowValues.add(post.getAddress());
        rowValues.add(post.getProduct().toString());
        rowValues.add(String.valueOf(post.getProduct().size()));
        rowValues.add(post.getMessage());

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
