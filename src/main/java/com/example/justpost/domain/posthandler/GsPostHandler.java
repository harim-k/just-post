package com.example.justpost.domain.posthandler;

import com.example.justpost.domain.post.InvoiceMap;
import com.example.justpost.domain.post.Post;
import com.example.justpost.domain.post.Posts;
import com.example.justpost.domain.utils.ExcelUtil;
import com.example.justpost.domain.utils.FileUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class GsPostHandler extends PostHandler {
    public static final String POST_FILE_NAME = "gs_post.xls";
    public static final String POST_TEMPLATE_FILE_NAME = "gs_post_template.xls";
    public static final int SHEET_INDEX = 0;
    public static final int HEADER_ROW_INDEX = 0;


    @Override
    public InvoiceMap getInvoiceMap(String postString) {
        InvoiceMap invoiceMap = new InvoiceMap();

        String[] strings = postString.replace("\r\n", "")
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

            invoiceMap.put(postcode, invoiceNumber);
        }

        return invoiceMap;
    }

    @Override
    public InvoiceMap getInvoiceMap(MultipartFile postFile) {
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
    Workbook convertToWorkbook(Posts posts) throws Exception {
        Workbook postWorkbook = new HSSFWorkbook();
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
                                  post.getProduct().toString(),
                                  post.getMessage()));
        rowValues.add("선불");

        return rowValues;
    }
}
