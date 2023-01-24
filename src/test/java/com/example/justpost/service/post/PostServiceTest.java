package com.example.justpost.service.post;

import com.example.justpost.domain.ConvertType;
import com.example.justpost.domain.handler.CuPostHandler;
import com.example.justpost.domain.handler.GsPostHandler;
import com.example.justpost.domain.utils.ExcelUtil;
import com.example.justpost.domain.utils.FileUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class PostServiceTest {
    private final static String TEST_ORDER_FILE_PATH = "src/test/resources/order/";
    private final static String TEST_POST_FILE_PATH = "src/test/resources/post/";
    @Autowired
    private PostService postService;

    @Test
    void test_네이버_발송처리() throws Exception {
        final String storeName = "네이버";

        test_대량발송_파일_변환(ConvertType.NAVER_POST, storeName);
    }

    @Test
    void test_쿠팡_발송처리() throws Exception {
        final String storeName = "쿠팡";

        test_대량발송_파일_변환(ConvertType.COUPANG_POST, storeName);
    }

    @Test
    void test_에이블리_발송처리() throws Exception {
        final String storeName = "에이블리";

        test_대량발송_파일_변환(ConvertType.ABLY_POST, storeName);
    }


    private void test_대량발송_파일_변환(ConvertType convertType,
                                 String storeName) throws Exception {
        final String orderFileName = storeName + "_주문.xlsx";
        final String orderFilePath = TEST_ORDER_FILE_PATH + orderFileName;

        final String actualGsPostFilePath = FileUtil.POST_FILE_PATH + storeName + "_" + GsPostHandler.POST_FILE_NAME;
        final String expectedGsPostFilePath = TEST_POST_FILE_PATH + storeName + "_" + GsPostHandler.POST_FILE_NAME;

        final String actualCuPostFilePath = FileUtil.POST_FILE_PATH + storeName + "_" + CuPostHandler.POST_FILE_NAME;
        final String expectedCuPostFilePath = TEST_POST_FILE_PATH + storeName + "_" + CuPostHandler.POST_FILE_NAME;

        int sheetIndex = 0;

        MockMultipartFile mockMultipartFile = getMockMultipartFile(orderFileName, orderFilePath);

        postService.convertAndSave(mockMultipartFile, convertType);

        assertThat(ExcelUtil.compare(actualGsPostFilePath, expectedGsPostFilePath, sheetIndex)).isTrue();
        assertThat(ExcelUtil.compare(actualCuPostFilePath, expectedCuPostFilePath, sheetIndex)).isTrue();
    }


    private MockMultipartFile getMockMultipartFile(String fileName, String path) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(path);
        return new MockMultipartFile(fileName, fileInputStream);
    }
}