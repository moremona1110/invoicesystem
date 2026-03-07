package com.codeb.invoicesystem.entity;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Invoice_no", nullable = false, unique = true)
    private Integer invoiceNo;

    @Column(name = "estimated_id", nullable = false)
    private Integer estimatedId;

    @Column(name = "chain_id", nullable = false)
    private Integer chainId;

    @Column(name = "service_details", length = 50)
    private String serviceDetails;

    @Column(name = "qty")
    private Integer qty;

    @Column(name = "cost_per_qty")
    private Float costPerQty;

    @Column(name = "amount_payable")
    private Float amountPayable;

    @Column(name = "balance")
    private Float balance;

    @Column(name = "date_of_payment")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateOfPayment;

    @Column(name = "date_of_service")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfService;

    @Column(name = "Delivery_details", length = 100)
    private String deliveryDetails;

    @Column(name = "email_id", length = 50)
    private String emailId;

    // Default Constructor
    public Invoice() {
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(Integer invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public Integer getEstimatedId() {
        return estimatedId;
    }

    public void setEstimatedId(Integer estimatedId) {
        this.estimatedId = estimatedId;
    }

    public Integer getChainId() {
        return chainId;
    }

    public void setChainId(Integer chainId) {
        this.chainId = chainId;
    }

    public String getServiceDetails() {
        return serviceDetails;
    }

    public void setServiceDetails(String serviceDetails) {
        this.serviceDetails = serviceDetails;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Float getCostPerQty() {
        return costPerQty;
    }

    public void setCostPerQty(Float costPerQty) {
        this.costPerQty = costPerQty;
    }

    public Float getAmountPayable() {
        return amountPayable;
    }

    public void setAmountPayable(Float amountPayable) {
        this.amountPayable = amountPayable;
    }

    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    public LocalDateTime getDateOfPayment() {
        return dateOfPayment;
    }

    public void setDateOfPayment(LocalDateTime dateOfPayment) {
        this.dateOfPayment = dateOfPayment;
    }

    public LocalDate getDateOfService() {
        return dateOfService;
    }

    public void setDateOfService(LocalDate dateOfService) {
        this.dateOfService = dateOfService;
    }

    public String getDeliveryDetails() {
        return deliveryDetails;
    }

    public void setDeliveryDetails(String deliveryDetails) {
        this.deliveryDetails = deliveryDetails;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
}
