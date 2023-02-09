package com.example.justpost.domain.store.post;

import com.example.justpost.domain.PostInfo;
import com.example.justpost.domain.utils.ExcelUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CoupangPostConverter extends PostConverter {
    public static final int SHEET_INDEX = 0;
    public static final int HEADER_ROW_INDEX = 0;

    @Override
    public List<PostInfo> convert(MultipartFile file) throws Exception {
        List<List<String>> postValues = new ArrayList<>();
        List<PostInfo> postInfos = new ArrayList<>();

        //TODO string[][]로 받아서 처리하도록 리팩터링
        Workbook orderWorkbook = WorkbookFactory.create(file.getInputStream());
        Sheet orderSheet = orderWorkbook.getSheetAt(SHEET_INDEX);
        Row orderHeaderRow = orderSheet.getRow(HEADER_ROW_INDEX);

        int nameColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "수취인이름");
        int postcodeColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "우편번호");

        int addressColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "수취인 주소");

        int contact1ColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "수취인전화번호");
        int contact2ColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "구매자전화번호");

        int optionColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "등록옵션명");
        int productColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "등록상품명");

        int countColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "구매수(수량)");

        int messageColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "배송메세지");

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


        if (product.contains(" ")) {
            product = product.split(" ")[1];
        }

        return String.join(" ",
                           StringUtils.equals(option, "단일상품") ? product : option,
                           count);
    }
}
