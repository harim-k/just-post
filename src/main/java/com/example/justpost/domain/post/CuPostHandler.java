package com.example.justpost.domain.post;

import com.example.justpost.domain.InvoiceNumberMap;
import com.example.justpost.domain.Post;
import com.example.justpost.domain.PostReservation;
import com.example.justpost.domain.utils.ExcelUtil;
import com.example.justpost.domain.utils.FileUtil;
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
public class CuPostHandler extends PostHandler {
    public static final String POST_FILE_NAME = "cu_대량발송.xlsx";
    public static final String POST_TEMPLATE_FILE_NAME = "cu_대량발송_템플릿.xlsx";
    public static final int SHEET_INDEX = 0;
    public static final int HEADER_ROW_INDEX = 0;

    private static Post makeInvoice(String string) {
        String name = string.split("전화번호")[0];
        String postcode = string.split("\\[")[1]
                .split("]")[0];
        String invoiceNumber = string.split("운송장번호")[1]
                .split("배송조회")[0];

        Post post = Post.builder()
                .name(name)
                .postcode(postcode)
                .invoiceNumber(invoiceNumber)
                .build();
        return post;
    }

    @Override
    public InvoiceNumberMap getInvoiceNumberMap(String postString) {
        InvoiceNumberMap invoiceNumberMap = new InvoiceNumberMap();

        String[] strings = postString.replace("\r\n", "")
                .replace("\n", "")
                .split("이름");

        strings = Arrays.copyOfRange(strings, 2, strings.length);

        for (String string : strings) {
            String name = string.split("전화번호")[0];
            String postcode = string.split("\\[")[1]
                    .split("]")[0];
            String invoiceNumber = string.split("운송장번호")[1]
                    .split("배송조회")[0];

            invoiceNumberMap.put(name, postcode, invoiceNumber);
        }

        return invoiceNumberMap;
    }

    @Override
    public InvoiceNumberMap getInvoiceNumberMap(MultipartFile postFile) {
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
        rowValues.add(postReservation.getPostcode());
        rowValues.add(postReservation.getAddress());
        rowValues.add(postReservation.getAddress());
        rowValues.add(postReservation.getContact1());
        rowValues.add(postReservation.getContact2());
        rowValues.add(String.join(" ",
                                  String.join(" ", postReservation.getProducts()),
                                  postReservation.getMessage()));
        rowValues.add("선불");
        return rowValues;
    }
}

