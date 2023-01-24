package com.example.justpost.domain.post.converter;

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
    public List<List<String>> convert(MultipartFile file) throws Exception {
        List<List<String>> postValues = new ArrayList<>();

        Workbook orderWorkbook = WorkbookFactory.create(file.getInputStream());
        Sheet orderSheet = orderWorkbook.getSheetAt(SHEET_INDEX);
        Row orderHeaderRow = orderSheet.getRow(HEADER_ROW_INDEX);

        int 수취인명ColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "수취인명");
        int 우편번호ColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "우편번호");
        int 기본배송지ColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "배송지 주소");

        int 수취인연락처1ColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "수취인 연락처");
        int 수취인연락처2ColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "연락처");

        int 수량ColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "수량");
        int 배송메세지ColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "배송 메모");

        int 옵션명ColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "옵션 정보");
        int 상품명ColumnIndex = ExcelUtil.getColumnIndex(orderHeaderRow, "상품명");

        // copy second ~ last row from order sheet
        for (int rowIndex = HEADER_ROW_INDEX + 1; rowIndex <= orderSheet.getLastRowNum(); rowIndex++) {
            Row orderRow = orderSheet.getRow(rowIndex);

            String 수취인명 = ExcelUtil.getValue(orderRow.getCell(수취인명ColumnIndex));
            String 우편번호 = ExcelUtil.getValue(orderRow.getCell(우편번호ColumnIndex));
            String 기본배송지 = ExcelUtil.getValue(orderRow.getCell(기본배송지ColumnIndex));

            String 수취인연락처1 = ExcelUtil.getValue(orderRow.getCell(수취인연락처1ColumnIndex));
            String 수취인연락처2 = ExcelUtil.getValue(orderRow.getCell(수취인연락처2ColumnIndex));

            String 수량 = ExcelUtil.getValue(orderRow.getCell(수량ColumnIndex));
            String 배송메세지 = ExcelUtil.getValue(orderRow.getCell(배송메세지ColumnIndex));
            String 옵션명 = ExcelUtil.getValue(orderRow.getCell(옵션명ColumnIndex));
            String 상품명 = ExcelUtil.getValue(orderRow.getCell(상품명ColumnIndex));

            // 특수문자 제거
            옵션명 = 옵션명.replace("&", "");
            상품명 = 상품명.replace("&", "");

            // 옵션정보
            if (옵션명.contains("/")) {
                옵션명 = 옵션명.split("/")[0];
            }

            String 품목 = 옵션명 != "" ? 옵션명 : 상품명;
            String 배송요청사항 = String.join(" ", 품목, 수량, 배송메세지);
            String 지불방법 = "선불";

            List<String> postRowValues = new ArrayList<>(
                    Arrays.asList(수취인명, 우편번호, 기본배송지, 기본배송지,
                                  수취인연락처1, 수취인연락처2, 배송요청사항, 지불방법));


            // 같은 주소인 경우 하나로 합치기
            addToPost(postValues, postRowValues, 수량, 품목);
        }

        // close workbook
        orderWorkbook.close();

        return postValues;
    }
}
