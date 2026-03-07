package com.codeb.invoicesystem.controller;

import com.codeb.invoicesystem.entity.Invoice;
import com.codeb.invoicesystem.service.EmailService;
import com.codeb.invoicesystem.service.InvoiceService;
import com.codeb.invoicesystem.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public List<Invoice> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Integer id) {
        return invoiceService.getInvoiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Invoice createInvoice(@RequestBody Invoice invoice) {
        return invoiceService.saveInvoice(invoice);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Invoice> updateInvoice(@PathVariable Integer id, @RequestBody Invoice invoiceDetails) {
        try {
            return ResponseEntity.ok(invoiceService.updateInvoice(id, invoiceDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Integer id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<InputStreamResource> downloadPdf(@PathVariable Integer id) {
        return invoiceService.getInvoiceById(id)
                .map(invoice -> {
                    ByteArrayInputStream bis = pdfService.generateInvoicePdf(invoice);
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Content-Disposition", "inline; filename=invoice_" + invoice.getInvoiceNo() + ".pdf");

                    return ResponseEntity
                            .ok()
                            .headers(headers)
                            .contentType(MediaType.APPLICATION_PDF)
                            .body(new InputStreamResource(bis));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/send-email")
    public ResponseEntity<String> sendEmail(@PathVariable Integer id) {
        return invoiceService.getInvoiceById(id)
                .map(invoice -> {
                    try {
                        ByteArrayInputStream bis = pdfService.generateInvoicePdf(invoice);
                        String subject = "Invoice #" + invoice.getInvoiceNo() + " from Your Company";
                        String body = "Dear Customer,\n\nPlease find attached your invoice #" + invoice.getInvoiceNo()
                                + ".\n\nThank you!";
                        String fileName = "invoice_" + invoice.getInvoiceNo() + ".pdf";

                        emailService.sendInvoiceWithAttachment(invoice.getEmailId(), subject, body, bis, fileName);
                        return ResponseEntity.ok("Email sent successfully to " + invoice.getEmailId());
                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Failed to send email: " + e.getMessage());
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
