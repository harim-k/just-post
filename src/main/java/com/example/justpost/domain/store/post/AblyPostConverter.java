package com.example.justpost.domain.store.post;

import com.example.justpost.domain.OrderColumnIndex;
import org.springframework.stereotype.Component;

import static com.example.justpost.domain.utils.StringUtil.getIndex;

@Component
public class AblyPostConverter extends PostConverter {
    public static final int ORDER_SHEET_INDEX = 1;
    public static final int HEADER_ROW_INDEX = 0;

    int getOrderSheetIndex() {
        return ORDER_SHEET_INDEX;
    }

    public int getHeaderRowIndex() {
        return HEADER_ROW_INDEX;
    }


    public String getProduct(String product,
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


    public OrderColumnIndex getOrderColumnIndex(String[][] orderSheet) {
        String[] orderHeaderRow = orderSheet[HEADER_ROW_INDEX];

        return OrderColumnIndex.builder()
                .nameColumnIndex(getIndex(orderHeaderRow, "수취인명"))
                .postcodeColumnIndex(getIndex(orderHeaderRow, "우편번호"))
                .addressColumnIndex(getIndex(orderHeaderRow, "배송지 주소"))
                .contact1ColumnIndex(getIndex(orderHeaderRow, "수취인 연락처"))
                .contact2ColumnIndex(getIndex(orderHeaderRow, "연락처"))
                .optionColumnIndex(getIndex(orderHeaderRow, "옵션 정보"))
                .productColumnIndex(getIndex(orderHeaderRow, "상품명"))
                .countColumnIndex(getIndex(orderHeaderRow, "수량"))
                .messageColumnIndex(getIndex(orderHeaderRow, "배송 메모"))
                .build();
    }
}
