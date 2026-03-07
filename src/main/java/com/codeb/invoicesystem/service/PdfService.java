package com.codeb.invoicesystem.service;

import com.codeb.invoicesystem.entity.Invoice;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.awt.Color;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

        public ByteArrayInputStream generateInvoicePdf(Invoice invoice) {
                Document document = new Document(PageSize.A4);
                ByteArrayOutputStream out = new ByteArrayOutputStream();

                try {
                        PdfWriter.getInstance(document, out);
                        document.open();

                        // Font styles
                        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
                        Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY);
                        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
                        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);

                        // Title
                        Paragraph title = new Paragraph("TAX INVOICE", headerFont);
                        title.setAlignment(Element.ALIGN_CENTER);
                        title.setSpacingAfter(20);
                        document.add(title);

                        // Company & Invoice Info Table
                        PdfPTable infoTable = new PdfPTable(2);
                        infoTable.setWidthPercentage(100);
                        infoTable.setSpacingBefore(10);
                        infoTable.setSpacingAfter(20);

                        // Left side: Company Info
                        PdfPCell companyCell = new PdfPCell();
                        companyCell.setBorder(Rectangle.NO_BORDER);
                        companyCell.addElement(new Paragraph("YOUR COMPANY NAME", subHeaderFont));
                        companyCell.addElement(new Paragraph("123 Business Street, City", normalFont));
                        companyCell.addElement(new Paragraph("GSTIN: 22AAAAA0000A1Z5", boldFont));
                        infoTable.addCell(companyCell);

                        // Right side: Invoice Info
                        PdfPCell invoiceInfoCell = new PdfPCell();
                        invoiceInfoCell.setBorder(Rectangle.NO_BORDER);
                        invoiceInfoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        invoiceInfoCell.addElement(new Paragraph("Invoice No: " + invoice.getInvoiceNo(), normalFont));
                        invoiceInfoCell.addElement(new Paragraph(
                                        "Date: " + invoice.getDateOfService()
                                                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                                        normalFont));
                        invoiceInfoCell.addElement(new Paragraph(
                                        "Customer Email: "
                                                        + (invoice.getEmailId() != null ? invoice.getEmailId() : "N/A"),
                                        normalFont));
                        infoTable.addCell(invoiceInfoCell);

                        document.add(infoTable);

                        // Service Details Table
                        PdfPTable detailsTable = new PdfPTable(5);
                        detailsTable.setWidthPercentage(100);
                        detailsTable.setWidths(new float[] { 3.0f, 1.0f, 1.5f, 1.5f, 1.5f });
                        detailsTable.setSpacingAfter(10);

                        // Table Header
                        String[] headers = { "Service Details", "Qty", "Rate", "GST (18%)", "Amount" };
                        for (String header : headers) {
                                PdfPCell headerCell = new PdfPCell(new Phrase(header, boldFont));
                                headerCell.setBackgroundColor(Color.LIGHT_GRAY);
                                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                headerCell.setPadding(5);
                                detailsTable.addCell(headerCell);
                        }

                        // Calculations
                        float subtotal = (invoice.getQty() != null ? invoice.getQty() : 0)
                                        * (invoice.getCostPerQty() != null ? invoice.getCostPerQty() : 0);
                        float gstAmount = subtotal * 0.18f;
                        float total = subtotal + gstAmount;

                        // Table Body
                        detailsTable.addCell(new PdfPCell(
                                        new Phrase(invoice.getServiceDetails() != null ? invoice.getServiceDetails()
                                                        : "N/A", normalFont)));
                        detailsTable.addCell(new PdfPCell(
                                        new Phrase(String.valueOf(invoice.getQty() != null ? invoice.getQty() : 0),
                                                        normalFont)));
                        detailsTable.addCell(new PdfPCell(new Phrase(
                                        String.format("%.2f",
                                                        invoice.getCostPerQty() != null ? invoice.getCostPerQty() : 0),
                                        normalFont)));
                        detailsTable.addCell(new PdfPCell(new Phrase(String.format("%.2f", gstAmount), normalFont)));
                        detailsTable.addCell(new PdfPCell(new Phrase(String.format("%.2f", total), normalFont)));

                        document.add(detailsTable);

                        // Summary Table
                        PdfPTable summaryTable = new PdfPTable(2);
                        summaryTable.setWidthPercentage(40);
                        summaryTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

                        summaryTable.addCell(new PdfPCell(new Phrase("Subtotal:", boldFont)));
                        summaryTable.addCell(new PdfPCell(new Phrase(String.format("%.2f", subtotal), normalFont)));

                        summaryTable.addCell(new PdfPCell(new Phrase("CGST (9%):", boldFont)));
                        summaryTable.addCell(
                                        new PdfPCell(new Phrase(String.format("%.2f", gstAmount / 2), normalFont)));

                        summaryTable.addCell(new PdfPCell(new Phrase("SGST (9%):", boldFont)));
                        summaryTable.addCell(
                                        new PdfPCell(new Phrase(String.format("%.2f", gstAmount / 2), normalFont)));

                        PdfPCell totalLabelCell = new PdfPCell(new Phrase("Total Amount:", boldFont));
                        totalLabelCell.setBackgroundColor(Color.LIGHT_GRAY);
                        summaryTable.addCell(totalLabelCell);

                        PdfPCell totalValueCell = new PdfPCell(new Phrase(String.format("%.2f", total), boldFont));
                        totalValueCell.setBackgroundColor(Color.LIGHT_GRAY);
                        summaryTable.addCell(totalValueCell);

                        document.add(summaryTable);

                        // Footer
                        Paragraph footer = new Paragraph("\n\nThank you for your business!", normalFont);
                        footer.setAlignment(Element.ALIGN_CENTER);
                        document.add(footer);

                        document.close();

                } catch (DocumentException ex) {
                        ex.printStackTrace();
                }

                return new ByteArrayInputStream(out.toByteArray());
        }
}
