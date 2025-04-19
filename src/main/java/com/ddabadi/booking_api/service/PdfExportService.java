package com.ddabadi.booking_api.service;

import com.ddabadi.booking_api.entity.Booking;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.List;
import java.util.stream.Stream;

@Service
public class PdfExportService {
    public void exportBookingsToPdf(List<Booking> bookings, OutputStream out) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph title = new Paragraph("Booking Report", font);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // spacer

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{4, 3, 2});

            addTableHeader(table);
            addTableRows(table, bookings);

            document.add(table);
            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("Customer Name", "Date", "Room Number")
                .forEach(header -> {
                    PdfPCell cell = new PdfPCell();
                    cell.setPhrase(new Phrase(header));
                    table.addCell(cell);
                });
    }

    private void addTableRows(PdfPTable table, List<Booking> bookings) {
        for (Booking booking : bookings) {
            table.addCell(booking.getCustomerName());
            table.addCell(booking.getStartTime().toString());
            table.addCell(booking.getEndTime().toString());
        }
    }
}
