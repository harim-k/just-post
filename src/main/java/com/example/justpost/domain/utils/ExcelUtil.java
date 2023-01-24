package com.example.justpost.domain.utils;

import org.apache.poi.ss.usermodel.*;
import org.thymeleaf.util.StringUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {

    public static List<Row> getRows(Sheet sheet) {
        List<Row> rows = new ArrayList<>();

        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            rows.add(sheet.getRow(i));
        }

        return rows;
    }

    public static String[][] workbookToArray(Workbook workbook, int sheetIndex) {
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        return sheetToArray(sheet);
    }

    public static String[][] sheetToArray(Sheet sheet) {
        int rowCount = sheet.getPhysicalNumberOfRows();
        int columnCount = sheet.getRow(0).getPhysicalNumberOfCells();
        String[][] data = new String[rowCount][columnCount];

        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                Cell cell = row.getCell(columnIndex);
                data[rowIndex][columnIndex] = getValue(cell);
            }
        }

        return data;
    }

    public static int getColumnIndex(Row row, String value) {
        int colCount = row.getPhysicalNumberOfCells();

        for (int columnIndex = 0; columnIndex < colCount; columnIndex++) {
            Cell cell = row.getCell(columnIndex);
            if (value.equals(getValue(cell))) {
                return columnIndex;
            }
        }

        return -1;
    }

    public static int getColumnIndex(Sheet sheet, int rowIndex, String value) {
        Row row = sheet.getRow(rowIndex);
        int colCount = row.getPhysicalNumberOfCells();

        for (int columnIndex = 0; columnIndex < colCount; columnIndex++) {
            Cell cell = row.getCell(columnIndex);
            if (value.equals(getValue(cell))) {
                return columnIndex;
            }
        }

        return -1;
    }

    public static String getValue(Cell cell) {
        if (cell == null) {
            return "";
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((int) cell.getNumericCellValue());
        }
        return cell.getStringCellValue();
    }

    public static List<String> getValues(Row row) {
        List<String> values = new ArrayList<>();

        for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
            values.add(getValue(row.getCell(i)));
        }

        return values;
    }

    public static void copyRow(Sheet srcSheet, Sheet dstSheet, int rowIndex) {
        copyRow(srcSheet.getRow(rowIndex), dstSheet.createRow(rowIndex));
    }

    public static void copyRow(Row srcRow, Row dstRow) {
        for (int i = 0; i < srcRow.getPhysicalNumberOfCells(); i++) {
            String value = getValue(srcRow.getCell(i));
            dstRow.createCell(i).setCellValue(value);
        }
    }

    public static void copyRow(Row srcRow, Row dstRow, int[] srcColumnIndices) {
        for (int i = 0; i < srcColumnIndices.length; i++) {
            String value = getValue(srcRow.getCell(srcColumnIndices[i]));
            dstRow.createCell(i).setCellValue(value);
        }
    }

    public static void setValues(Sheet sheet, List<List<String>> values, int startRowIndex) {
        for (int i = 0; i < values.size(); i++) {
            Row row = sheet.createRow(startRowIndex + i);
            setRow(row, values.get(i));
        }
    }

    public static void setRow(Row row, List<String> values) {
        for (int i = 0; i < values.size(); i++) {
            row.createCell(i).setCellValue(values.get(i));
        }
    }

    public static void save(Workbook postWorkbook, String path) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(path);
        postWorkbook.write(outputStream);
        outputStream.close();
    }

    public static boolean compare(String filePath1, String filePath2, int sheetIndex) throws Exception {
        Workbook workbook1 = WorkbookFactory.create(new FileInputStream(filePath1));
        Workbook workbook2 = WorkbookFactory.create(new FileInputStream(filePath2));

        String[][] values1 = workbookToArray(workbook1, sheetIndex);
        String[][] values2 = workbookToArray(workbook2, sheetIndex);

        if (values1.length != values2.length) {
            return false;
        }

        for (int i = 0; i < values1.length; i++) {
            for (int j = 0; j < values1[i].length; j++) {
                if (!StringUtils.equals(values1[i][j], values2[i][j])) {
                    return false;
                }
            }
        }

        // Close the workbooks
        workbook1.close();
        workbook2.close();

        return true;
    }
}

