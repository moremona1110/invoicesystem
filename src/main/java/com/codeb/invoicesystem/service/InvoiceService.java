package com.codeb.invoicesystem.service;

import com.codeb.invoicesystem.entity.Invoice;
import com.codeb.invoicesystem.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Optional<Invoice> getInvoiceById(Integer id) {
        return invoiceRepository.findById(id);
    }

    public Invoice saveInvoice(Invoice invoice) {
        // Calculate amount payable if not provided
        if (invoice.getQty() != null && invoice.getCostPerQty() != null) {
            float subTotal = invoice.getQty() * invoice.getCostPerQty();
            // Assuming 18% GST (9% CGST + 9% SGST)
            float gstRate = 0.18f;
            float totalWithTax = subTotal * (1 + gstRate);
            invoice.setAmountPayable(totalWithTax);

            // If balance is not set, initialize it with amount payable
            if (invoice.getBalance() == null) {
                invoice.setBalance(totalWithTax);
            }
        }
        return invoiceRepository.save(invoice);
    }

    public void deleteInvoice(Integer id) {
        invoiceRepository.deleteById(id);
    }

    public Invoice updateInvoice(Integer id, Invoice invoiceDetails) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));

        invoice.setInvoiceNo(invoiceDetails.getInvoiceNo());
        invoice.setEstimatedId(invoiceDetails.getEstimatedId());
        invoice.setChainId(invoiceDetails.getChainId());
        invoice.setServiceDetails(invoiceDetails.getServiceDetails());
        invoice.setQty(invoiceDetails.getQty());
        invoice.setCostPerQty(invoiceDetails.getCostPerQty());
        invoice.setAmountPayable(invoiceDetails.getAmountPayable());
        invoice.setBalance(invoiceDetails.getBalance());
        invoice.setDateOfPayment(invoiceDetails.getDateOfPayment());
        invoice.setDateOfService(invoiceDetails.getDateOfService());
        invoice.setDeliveryDetails(invoiceDetails.getDeliveryDetails());
        invoice.setEmailId(invoiceDetails.getEmailId());

        return saveInvoice(invoice);
    }
}
