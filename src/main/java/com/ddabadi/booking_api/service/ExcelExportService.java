package com.ddabadi.booking_api.service;

import com.ddabadi.booking_api.entity.Booking;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.autoconfigure.web.format.DateTimeFormatters;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelExportService {
    public void exportBookingsToExcel(List<Booking> bookings, OutputStream out) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Daily Report");

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            // Header
            Row header = sheet.createRow(0);
            header.setRowStyle(cellStyle);
            header.createCell(0).setCellValue("Customer Name");
            header.createCell(1).setCellValue("Phone");
            header.createCell(2).setCellValue("Date");
            header.createCell(3).setCellValue("Room Number");
            header.createCell(4).setCellValue("Start Time");
            header.createCell(5).setCellValue("End Time");
            header.createCell(6).setCellValue("Event Name");
            header.createCell(7).setCellValue("Number of participants");
            header.createCell(8).setCellValue("Notes");

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            // Data
            int rowNum = 1;
            for (Booking booking : bookings) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(booking.getCustomerName());
                row.createCell(1).setCellValue(booking.getPhoneNumber());
                row.createCell(2).setCellValue(booking.getStartTime().format(dateFormatter) );
                row.createCell(3).setCellValue(booking.getRoom().getName());
                row.createCell(4).setCellValue(booking.getStartTime().format (timeFormatter));
                row.createCell(5).setCellValue(booking.getEndTime().format(timeFormatter));
                row.createCell(6).setCellValue(booking.getEventName());
                row.createCell(7).setCellValue(booking.getNumberOfParticipants() == null ? 0 :booking.getNumberOfParticipants() );
                row.createCell(8).setCellValue(booking.getNotes());
                row.getCell(7).setCellStyle(cellStyle);
            }

            workbook.write(out);
        } catch (Exception e) {
            throw new RuntimeException("Failed to export Excel", e);
        }
    }
}
