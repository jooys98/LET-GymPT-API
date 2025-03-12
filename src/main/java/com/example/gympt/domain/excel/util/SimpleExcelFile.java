package com.example.gympt.domain.excel.util;


import com.example.gympt.domain.excel.dto.ExcelColumn;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;

public class SimpleExcelFile<T> {

    private static final SpreadsheetVersion supplyExcelVersion = SpreadsheetVersion.EXCEL2007;
    private static final int ROW_START_INDEX = 0;

    private SXSSFWorkbook wb;
    private Sheet sheet;

    public SimpleExcelFile(List<T> data, String sheetName) {
        validateIsEmpty(data);
        validateMaxRow(data);
        this.wb = new SXSSFWorkbook();
        renderExcel(data, sheetName);
        applyCellStyle();
    }

    private void validateIsEmpty(List<T> data) {
        if (data.isEmpty()) {
            throw new RuntimeException("Data is empty");
        }
    }

    private void applyCellStyle() {
        applyCellSize();
        applyBorderStyle();
        applyHeaderStyle();
    }

    private void applyBorderStyle() {
        CellStyle style = wb.createCellStyle();

        // border
        setBorderStyle(style, BorderStyle.THIN);

        for (Row row : sheet) {
            for (Cell cell : row) {
                cell.setCellStyle(style);
            }
        }
    }

    private void setBorderStyle(CellStyle style, BorderStyle borderStyle) {
        style.setBorderTop(borderStyle);
        style.setBorderBottom(borderStyle);
        style.setBorderLeft(borderStyle);
        style.setBorderRight(borderStyle);
    }

    private void applyHeaderStyle() {
        Row row = sheet.getRow(0);

        CellStyle style = wb.createCellStyle();

        // font
        setFontBold(style);

        // background color
        setBackgroundColor(style, IndexedColors.SKY_BLUE.getIndex());

        // border
        setBorderStyle(style, BorderStyle.THIN);

        for (int i = 0; i < row.getLastCellNum(); i++) {
            row.getCell(i).setCellStyle(style);
        }
    }

    private void setBackgroundColor(CellStyle style, short color) {
        style.setFillForegroundColor(color);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }

    private void setFontBold(CellStyle style) {
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
    }

    private void applyCellSize() {
        for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
            sheet.setColumnWidth(i, 4096);
        }
    }

    private void validateMaxRow(List<T> data) {
        int maxRows = supplyExcelVersion.getMaxRows();
        if (data.size() > maxRows) {
            throw new RuntimeException("엑셀 파일 생성 중 오류가 발생했습니다.");
        }
    }

    private void renderExcel(List<T> data, String sheetName) {
        // Create sheet and render headers
        sheet = wb.createSheet(sheetName);

        // Render Header
        renderHeaders(data.get(0), ROW_START_INDEX);

        // Render Body
        int rowIndex = ROW_START_INDEX + 1;
        for (T renderedData : data) {
            renderBody(renderedData, rowIndex++);
        }
    }
    private void renderHeaders(T data, int rowIndex) {
        Row row = sheet.createRow(rowIndex);

        Field[] fields = data.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Cell cell = row.createCell(i);
            fields[i].setAccessible(true);
            renderCellValue(cell, fields[i].getAnnotation(ExcelColumn.class).header());
        }
    }

    private void renderBody(T data, int rowIndex) {
        Row row = sheet.createRow(rowIndex);

        Field[] fields = data.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                Cell cell = row.createCell(i);
                fields[i].setAccessible(true);
                renderCellValue(cell, fields[i].get(data));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("엑셀 파일 생성 중 오류가 발생했습니다.");
            }
        }
    }

    private void renderCellValue(Cell cell, Object cellValue) {
        if (cellValue instanceof Number) {
            Number numberValue = (Number) cellValue;
            cell.setCellValue(numberValue.doubleValue());
            return;
        }
        cell.setCellValue(cellValue == null ? "" : cellValue.toString());
    }

    public void write(OutputStream stream) throws IOException {
        wb.write(stream);
        wb.close();
        wb.dispose();
        stream.close();
    }

}
