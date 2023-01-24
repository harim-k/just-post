package com.example.justpost.service.afterPost;

import com.example.justpost.domain.ConvertType;
import com.example.justpost.domain.utils.ExcelUtil;
import com.example.justpost.domain.utils.FileUtil;
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
class AfterPostServiceTest {
    private final static String TEST_ORDER_FILE_PATH = "src/test/resources/order/";
    private final static String TEST_AFTER_POST_FILE_PATH = "src/test/resources/afterPost/";
    @Autowired
    private AfterPostService afterPostService;

    @Test
    void test_네이버_발송처리_파일_변환() throws Exception {
        final String orderFileName = "네이버_주문.xlsx";
        final String afterPostFileName = "네이버_발송처리.xls";
        final String afterPostStringFileName = "네이버_택배발송정보";

        test_발송처리_파일_변환(ConvertType.NAVER_AFTER_POST, orderFileName,
                        afterPostFileName, afterPostStringFileName);
    }

    @Test
    void test_쿠팡_발송처리_파일_변환() throws Exception {
        final String orderFileName = "쿠팡_주문.xlsx";
        final String afterPostFileName = "쿠팡_발송처리.xlsx";
        final String afterPostStringFileName = "쿠팡_택배발송정보";

        test_발송처리_파일_변환(ConvertType.COUPANG_AFTER_POST, orderFileName,
                        afterPostFileName, afterPostStringFileName);
    }

    @Test
    void test_에이블리_발송처리_파일_변환() throws Exception {
        final String orderFileName = "에이블리_주문.xlsx";
        final String afterPostFileName = "에이블리_발송처리.xlsx";
        final String afterPostStringFileName = "에이블리_택배발송정보";

        test_발송처리_파일_변환(ConvertType.ABLY_AFTER_POST, orderFileName,
                        afterPostFileName, afterPostStringFileName);
    }

    void test_발송처리_파일_변환(ConvertType convertType,
                         String orderFileName,
                         String afterPostFileName,
                         String afterPostStringFileName) throws Exception {
        final String orderFilePath = TEST_ORDER_FILE_PATH + orderFileName;
        final String afterPostStringFilePath = TEST_AFTER_POST_FILE_PATH + afterPostStringFileName;
        final String afterPostString = new String(Files.readAllBytes(Paths.get(afterPostStringFilePath)));

        final String actualAfterPostFilePath = FileUtil.AFTER_POST_FILE_PATH + afterPostFileName;
        final String expectedAfterPostFilePath = TEST_AFTER_POST_FILE_PATH + afterPostFileName;

        int sheetIndex = convertType != ConvertType.ABLY_AFTER_POST ? 0 : 1;

        MockMultipartFile mockMultipartFile = getMockMultipartFile(orderFileName, orderFilePath);

        afterPostService.convertAndSave(mockMultipartFile, afterPostString, convertType);

        assertThat(ExcelUtil.compare(actualAfterPostFilePath, expectedAfterPostFilePath, sheetIndex)).isTrue();
    }


    private MockMultipartFile getMockMultipartFile(String fileName, String path) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(path);
        return new MockMultipartFile(fileName, fileInputStream);
    }
}