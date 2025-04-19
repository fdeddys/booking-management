package com.ddabadi.booking_api.service;

import com.ddabadi.booking_api.entity.Booking;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.List;

@Service
public class ExcelExportService {
    public void exportBookingsToExcel(List<Booking> bookings, OutputStream out) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Bookings");

            // Header
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Customer Name");
            header.createCell(1).setCellValue("Date");
            header.createCell(2).setCellValue("Room Number");

            // Data
            int rowNum = 1;
            for (Booking booking : bookings) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(booking.getCustomerName());
                row.createCell(1).setCellValue(booking.getStartTime().toString());
                row.createCell(2).setCellValue(booking.getEndTime().toString());
            }

            workbook.write(out);
        } catch (Exception e) {
            throw new RuntimeException("Failed to export Excel", e);
        }
    }
}
