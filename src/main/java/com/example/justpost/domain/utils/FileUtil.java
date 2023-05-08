package com.example.justpost.domain.utils;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static final String POST_FILE_PATH = "src/main/resources/post/";
    public static final String AFTER_POST_FILE_PATH = "src/main/resources/afterPost/";
    public static final String MERGE_FILE_PATH = "src/main/resources/merge/";
    public static final String POST_TEMPLATE_FILE_PATH = "src/main/resources/templates/post/";
    public static final String AFTER_POST_TEMPLATE_FILE_PATH = "src/main/resources/templates/afterPost/";
    public static final String MERGED_EXCEL_FILE_NAME = "mergedExcelFile.xlsx";


    public static void downloadFile(HttpServletResponse response,
                                    String filePath) throws IOException {
        String fileName = filePath.split("/")[4];
        downloadFile(response, filePath, fileName);
    }

    public static void downloadFile(HttpServletResponse response,
                                    String filePath,
                                    String fileName) throws IOException {
        // 파일명 한글 사용을 위해 인코딩
        String encodedFilename = URLEncoder.encode(fileName, "UTF-8");

        // Set the content type and attachment header.
        response.addHeader("Content-disposition", "attachment;filename*=UTF-8''"
                + getNowToString() + "_" + encodedFilename);
        response.setContentType("application/ms-excel");

        // Read the file from the resources directory.
        InputStream inputStream = new FileInputStream(filePath);

        // Copy the stream to the response's output stream.
        IOUtils.copy(inputStream, response.getOutputStream());
        response.flushBuffer();
    }

    private static String getNowToString() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(System.currentTimeMillis());
    }


    public static void mergeExcelFiles(HttpServletResponse response,
                                       List<MultipartFile> files) throws IOException {
        final String fileName = MERGED_EXCEL_FILE_NAME;
        final String filePath = MERGE_FILE_PATH + MERGED_EXCEL_FILE_NAME;

        // Create a new workbook and sheet to hold the merged data
        XSSFWorkbook mergedWorkbook = new XSSFWorkbook();
        XSSFSheet mergedSheet = mergedWorkbook.createSheet();

        copyHeaderRow(mergedSheet, files.get(0));

        for (MultipartFile file : files) {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            List<Row> rows = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                rows.add(sheet.getRow(i));
            }

            mergedSheet.copyRows(rows, mergedSheet.getLastRowNum() + 1,
                                 new CellCopyPolicy());
            workbook.close();
        }

        ExcelUtil.save(mergedWorkbook, filePath);

        mergedWorkbook.close();

        downloadFile(response, filePath, fileName);
    }

    private static void copyHeaderRow(XSSFSheet mergedSheet,
                                      MultipartFile file) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        List<Row> rows = new ArrayList<>();
        rows.add(sheet.getRow(0));

        mergedSheet.copyRows(rows, mergedSheet.getLastRowNum() + 1,
                             new CellCopyPolicy());
    }
}
