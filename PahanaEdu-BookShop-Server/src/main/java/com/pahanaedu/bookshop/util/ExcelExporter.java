package com.pahanaedu.bookshop.util;

import com.pahanaedu.bookshop.model.report.SalesSummaryDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ExcelExporter {

    public static ByteArrayOutputStream generateSalesReport(SalesSummaryDTO summary) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sales Summary");

        int rowIndex = 0;
        Row header = sheet.createRow(rowIndex++);
        header.createCell(0).setCellValue("Total Revenue (LKR)");
        header.createCell(1).setCellValue("Total Invoices");
        header.createCell(2).setCellValue("Best Selling Item");

        Row dataRow = sheet.createRow(rowIndex++);
        dataRow.createCell(0).setCellValue((RichTextString) summary.getTotalRevenue());
        dataRow.createCell(1).setCellValue(summary.getTotalInvoices());
        dataRow.createCell(2).setCellValue(summary.getBestSellingItemName());

        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream;
    }
}
