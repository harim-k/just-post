package com.example.justpost.domain.store.post;

import com.example.justpost.domain.PostInfo;
import com.example.justpost.domain.utils.ExcelUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class AblyPostConverter extends PostConverter {
    public static final int SHEET_INDEX = 1;
    public static final int HEADER_ROW_INDEX = 0;


    @Override
    public List<PostInfo> convert(MultipartFile file) throws Exception {
        List<List<String>> postValues = new ArrayList<>();
        List<PostInfo> postInfos = new ArrayList<>();

        Workbook orderWorkbook = WorkbookFactory.create(file.getInputStream());
        Sheet orderSheet = orderWorkbook.getSheetAt(SHEET_INDEX);
        Row orderHeaderRow = orderSheet.getRow(HEADER_ROW_INDEX);

        int nameColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "수취인명");
        int postcodeColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "우편번호");

        int addressColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "배송지 주소");

        int contact1ColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "수취인 연락처");
        int contact2ColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "연락처");

        int optionColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "옵션 정보");
        int productColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "상품명");

        int countColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "수량");

        int messageColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "배송 메모");


        // copy second ~ last row from order sheet
        for (int rowIndex = HEADER_ROW_INDEX + 1; rowIndex <= orderSheet.getLastRowNum(); rowIndex++) {
            final Row orderRow = orderSheet.getRow(rowIndex);

            final String name = ExcelUtil.getValue(orderRow.getCell(nameColumnIndex));
            final String postcode = ExcelUtil.getValue(orderRow.getCell(postcodeColumnIndex));

            final String address = ExcelUtil.getValue(orderRow.getCell(addressColumnIndex));

            final String contact1 = ExcelUtil.getValue(orderRow.getCell(contact1ColumnIndex));
            final String contact2 = ExcelUtil.getValue(orderRow.getCell(contact2ColumnIndex));

            final String product = ExcelUtil.getValue(orderRow.getCell(productColumnIndex));
            final String option = ExcelUtil.getValue(orderRow.getCell(optionColumnIndex));

            final String count = ExcelUtil.getValue(orderRow.getCell(countColumnIndex));

            final String message = ExcelUtil.getValue(orderRow.getCell(messageColumnIndex));
            final String cost = "선불";

            final String productInfo = getProductInfo(product, option, count);


            List<String> postRowValues = new ArrayList<>(
                    Arrays.asList(name, postcode, address, address,
                                  contact1, contact2, message, cost));

            PostInfo postInfo = PostInfo.builder()
                    .name(name)
                    .postcode(postcode)
                    .address(address)
                    .contact1(contact1)
                    .contact2(contact2)
                    .productInfos(new ArrayList<>(List.of(productInfo)))
                    .message(message)
                    .build();

            // 같은 주소인 경우 하나로 합치기
            addToPost(postValues, postRowValues, count, product);
            addToPostInfo(postInfos, postInfo);
        }

        // close workbook
        orderWorkbook.close();

        return postInfos;
    }

    private String getProductInfo(String product,
                                  String option,
                                  String count) {
        // 특수문자 제거
        option = option.replace("&", "");
        product = product.replace("&", "");

        // 옵션정보
        if (option.contains("/")) {
            option = option.split("/")[0];
        }

        return String.join(" ",
                           option != "" ? option : product,
                           count);
    }
}
