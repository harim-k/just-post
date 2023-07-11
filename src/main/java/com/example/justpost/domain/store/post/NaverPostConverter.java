package com.example.justpost.domain.store.post;

import com.example.justpost.domain.OrderColumnIndex;
import com.example.justpost.domain.utils.ExcelUtil;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import static com.example.justpost.domain.utils.StringUtil.getIndex;

@Component
public class NaverPostConverter extends PostConverter {

    public static final int SHEET_INDEX = 0;
    public static final int HEADER_ROW_INDEX = 1;

    int getOrderSheetIndex() {
        return SHEET_INDEX;
    }

    public int getHeaderRowIndex() {
        return HEADER_ROW_INDEX;
    }


    public String[][] getOrderSheet(MultipartFile orderFile) throws Exception {
        Workbook orderWorkbook = decryptExcelFile(orderFile);
        String[][] orderSheet = ExcelUtil.workbookToArray(
                orderWorkbook,
                SHEET_INDEX,
                HEADER_ROW_INDEX);

        // close workbook
        orderWorkbook.close();
        return orderSheet;
    }

   public String getProduct(String product,
                      String option,
                      String count) {
        // 특수문자 제거
        option = option.replace("&", "");
        product = product.replace("&", "");

        // 단품 제거
        option = option.replace("단품", "");
        product = product.replace("단품", "");

        // 옵션명 제거
        if (option.contains(": ")) {
            option = option.split(": ")[1];
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
                .addressColumnIndex(getIndex(orderHeaderRow, "통합배송지"))
                .contact1ColumnIndex(getIndex(orderHeaderRow, "수취인연락처1"))
                .contact2ColumnIndex(getIndex(orderHeaderRow, "수취인연락처2"))
                .optionColumnIndex(getIndex(orderHeaderRow, "옵션정보"))
                .productColumnIndex(getIndex(orderHeaderRow, "상품명"))
                .countColumnIndex(getIndex(orderHeaderRow, "수량"))
                .messageColumnIndex(getIndex(orderHeaderRow, "배송메세지"))
                .build();
    }


    private Workbook decryptExcelFile(MultipartFile file) throws Exception {
        POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
        EncryptionInfo info = new EncryptionInfo(fs);
        Decryptor decryptor = Decryptor.getInstance(info);

        if (!decryptor.verifyPassword("1111")) {
            throw new Exception("Incorrect password");
        }

        return WorkbookFactory.create(decryptor.getDataStream(fs));
    }
}
