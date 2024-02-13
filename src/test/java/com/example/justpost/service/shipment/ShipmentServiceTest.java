package com.example.justpost.service.shipment;

import com.example.justpost.domain.post.ConvertType;
import com.example.justpost.domain.store.shipment.ShipmentConverter;
import com.example.justpost.domain.store.shipment.ShipmentConverterFactory;
import com.example.justpost.domain.utils.ExcelUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class ShipmentServiceTest {
    private final static String TEST_ORDER_FILE_PATH = "src/test/resources/order/";
    private final static String TEST_AFTER_POST_FILE_PATH = "src/test/resources/after-post/";
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private ShipmentConverterFactory shipmentConverterFactory;

    @Test
    void test_네이버_발송처리_파일_변환() throws Exception {
        final String orderFileName = "네이버_주문.xlsx";
        final String shipmentFileName = "네이버_발송처리.xls";
        final String shipmentStringFileName = "네이버_택배발송정보";

        test_발송처리_파일_변환(ConvertType.NAVER_AFTER_POST, orderFileName,
                        shipmentFileName, shipmentStringFileName);
    }

    @Test
    void test_쿠팡_발송처리_파일_변환() throws Exception {
        final String orderFileName = "쿠팡_주문.xlsx";
        final String shipmentFileName = "쿠팡_발송처리.xlsx";
        final String shipmentStringFileName = "쿠팡_택배발송정보";

        test_발송처리_파일_변환(ConvertType.COUPANG_AFTER_POST, orderFileName,
                        shipmentFileName, shipmentStringFileName);
    }

    @Test
    void test_에이블리_발송처리_파일_변환() throws Exception {
        final String orderFileName = "에이블리_주문.xlsx";
        final String shipmentFileName = "에이블리_발송처리.xlsx";
        final String shipmentStringFileName = "에이블리_택배발송정보";

        test_발송처리_파일_변환(ConvertType.ABLY_AFTER_POST, orderFileName,
                        shipmentFileName, shipmentStringFileName);
    }

    @Test
    void test_에이블리_발송처리_파일_변환2() throws Exception {
        final String orderFileName = "에이블리_주문2.xlsx";
        final String shipmentFileName = "에이블리_발송처리2.xlsx";
        final String postFileName = "에이블리_택배발송정보2.xls";

        test_발송처리_파일_변환2(ConvertType.ABLY_AFTER_POST, orderFileName,
                        shipmentFileName, postFileName);
    }

    void test_발송처리_파일_변환(ConvertType convertType,
                         String orderFileName,
                         String shipmentFileName,
                         String shipmentStringFileName) throws Exception {
        ShipmentConverter shipmentConverter = shipmentConverterFactory.get(convertType);

        final String orderFilePath = TEST_ORDER_FILE_PATH + orderFileName;

        final String shipmentStringFilePath = TEST_AFTER_POST_FILE_PATH + shipmentStringFileName;
        final String shipmentString = new String(Files.readAllBytes(Paths.get(shipmentStringFilePath)));

        final String actualShipmentFilePath = shipmentConverter.getShipmentFilePath();
        final String expectedShipmentFilePath = TEST_AFTER_POST_FILE_PATH + shipmentFileName;

        int sheetIndex = convertType != ConvertType.ABLY_AFTER_POST ? 0 : 1;

        MockMultipartFile orderFile = getMockMultipartFile(orderFileName, orderFilePath);

        shipmentService.convertAndSave(
                orderFile, convertType,
                null, shipmentString);

        assertThat(ExcelUtil.compare(actualShipmentFilePath, expectedShipmentFilePath, sheetIndex)).isTrue();
    }

    void test_발송처리_파일_변환2(ConvertType convertType,
                         String orderFileName,
                         String shipmentFileName,
                         String postFileName) throws Exception {
        ShipmentConverter shipmentConverter = shipmentConverterFactory.get(convertType);

        final String orderFilePath = TEST_ORDER_FILE_PATH + orderFileName;
        final String postFilePath = TEST_AFTER_POST_FILE_PATH + postFileName;

        final String actualShipmentFilePath = shipmentConverter.getShipmentFilePath();
        final String expectedShipmentFilePath = TEST_AFTER_POST_FILE_PATH + shipmentFileName;

        int sheetIndex = convertType != ConvertType.ABLY_AFTER_POST ? 0 : 1;

        MockMultipartFile orderFile = getMockMultipartFile(orderFileName, orderFilePath);
        MockMultipartFile postFile = convertType == ConvertType.ABLY_AFTER_POST ?
                getMockMultipartFile(postFileName, postFilePath) : null;

        shipmentService.convertAndSave(
                orderFile, convertType,
                postFile, "");

        assertThat(ExcelUtil.compare(actualShipmentFilePath, expectedShipmentFilePath, sheetIndex)).isTrue();
    }


    private MockMultipartFile getMockMultipartFile(String fileName,
                                                   String filePath) throws IOException {
        if (fileName == null || filePath == null) {
            return null;
        }

        FileInputStream fileInputStream = new FileInputStream(filePath);
        return new MockMultipartFile(fileName, fileInputStream);
    }
}