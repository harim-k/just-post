package com.example.justpost.domain.store.post;

import com.example.justpost.domain.post.OrderColumnIndex;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import static com.example.justpost.domain.utils.StringUtil.getIndex;

@Component
public class CoupangPostConverter extends PostConverter {
    public static final int SHEET_INDEX = 0;
    public static final int HEADER_ROW_INDEX = 0;


    int getOrderSheetIndex() {
        return SHEET_INDEX;
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


        if (product.contains(" ")) {
            product = product.split(" ")[1];
        }

        return String.join(" ",
                           StringUtils.equals(option, "단일상품") ? product : option,
                           count);
    }

    public OrderColumnIndex getOrderColumnIndex(String[][] orderSheet) {
        String[] orderHeaderRow = orderSheet[HEADER_ROW_INDEX];

        return OrderColumnIndex.builder()
                .nameColumnIndex(getIndex(orderHeaderRow, "수취인이름"))
                .postcodeColumnIndex(getIndex(orderHeaderRow, "우편번호"))
                .addressColumnIndex(getIndex(orderHeaderRow, "수취인 주소"))
                .contact1ColumnIndex(getIndex(orderHeaderRow, "수취인전화번호"))
                .contact2ColumnIndex(getIndex(orderHeaderRow, "구매자전화번호"))
                .optionColumnIndex(getIndex(orderHeaderRow, "등록옵션명"))
                .productColumnIndex(getIndex(orderHeaderRow, "등록상품명"))
                .countColumnIndex(getIndex(orderHeaderRow, "구매수(수량)"))
                .messageColumnIndex(getIndex(orderHeaderRow, "배송메세지"))
                .build();
    }
}
