package com.codeb.invoicesystem.repository;

import com.codeb.invoicesystem.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    
    // Custom query to find invoice by invoice number
    Invoice findByInvoiceNo(Integer invoiceNo);
    
    // Find by estimate id
    List<Invoice> findByEstimatedId(Integer estimatedId);
    
    // Additional methods can be added if needed, like finding by chainId
    List<Invoice> findByChainId(Integer chainId);
    
    // A simple search query combining different fields could be implemented using @Query
}
