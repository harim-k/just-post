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


    public static void mergeExcelFiles(MultipartFile file1,
                                       MultipartFile file2) throws IOException {
        // Create a new workbook and sheet to hold the merged data
        XSSFWorkbook mergedWorkbook = new XSSFWorkbook();
        XSSFSheet mergedSheet = mergedWorkbook.createSheet();

        // Read excel files
        Workbook workbook1 = WorkbookFactory.create(file1.getInputStream());
        Sheet sheet1 = workbook1.getSheetAt(0);

        Workbook workbook2 = WorkbookFactory.create(file2.getInputStream());
        Sheet sheet2 = workbook1.getSheetAt(0);

        List<Row> rows1 = new ArrayList<>();
        List<Row> rows2 = new ArrayList<>();

        // Copy the data from the first excel file to the new sheet
        for (int i = 0; i <= sheet1.getLastRowNum(); i++) {
            rows1.add(sheet1.getRow(i));
        }

        for (int i = 1; i <= sheet2.getLastRowNum(); i++) {
            rows2.add(sheet2.getRow(i));
        }

        mergedSheet.copyRows(rows1, 0, new CellCopyPolicy());
        mergedSheet.copyRows(rows2, rows1.size(), new CellCopyPolicy());


        // Write the merged data to a new excel file
        FileOutputStream outputStream = new FileOutputStream(MERGED_EXCEL_FILE_NAME);
        mergedWorkbook.write(outputStream);
        outputStream.close();

        // Close the workbooks
        workbook1.close();
        workbook2.close();
        mergedWorkbook.close();
    }
}
