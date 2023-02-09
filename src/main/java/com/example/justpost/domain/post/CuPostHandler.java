package com.example.justpost.domain.post;

import com.example.justpost.domain.Invoice;
import com.example.justpost.domain.Post;
import com.example.justpost.domain.utils.ExcelUtil;
import com.example.justpost.domain.utils.FileUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CuPostHandler extends PostHandler {
    public static final String POST_FILE_NAME = "cu_대량발송.xlsx";
    public static final String POST_TEMPLATE_FILE_NAME = "cu_대량발송_템플릿.xlsx";
    public static final int SHEET_INDEX = 0;
    public static final int HEADER_ROW_INDEX = 0;

    @Override
    public void saveAsPostFile(List<Post> postValues,
                               String storeName) throws Exception {
        super.saveAsPostFile(postValues, storeName);
    }

    @Override
    public List<Invoice> extractInvoices(String 택배예약현황String) {
        List<Invoice> invoices = new ArrayList<>();
        String[] strings = 택배예약현황String.replace("\r\n", "")
                .replace("\n", "")
                .split("이름");

        strings = Arrays.copyOfRange(strings, 2, strings.length);

        for (String string : strings) {
            String name = string.split("전화번호")[0];
            String postcode = string.split("\\[")[1]
                    .split("]")[0];
            String invoiceNumber = string.split("운송장번호")[1]
                    .split("배송조회")[0];

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
        rowValues.add(post.getPostcode());
        rowValues.add(post.getAddress());
        rowValues.add(post.getAddress());
        rowValues.add(post.getContact1());
        rowValues.add(post.getContact2());
        rowValues.add(String.join(" ",
                                  String.join(" ", post.getProductInfos()),
                                  post.getMessage()));
        rowValues.add("선불");
        return rowValues;
    }
}

