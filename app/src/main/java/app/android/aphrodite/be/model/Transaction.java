package app.android.aphrodite.be.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

@Entity
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "customerName")
    private String customerName;

    @ColumnInfo(name = "transactionDate")
    private String transactionDate;

    @ColumnInfo(name = "paymentType")
    private String paymentType;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "discount")
    private Double discount;

    @ColumnInfo(name = "note")
    private String note;

    @ColumnInfo(name = "subTotal")
    private Double subTotal;

    @ColumnInfo(name = "grandTotal")
    private Double grandTotal;

    @ColumnInfo(name = "isActive")
    private Boolean isActive;

    @Ignore
    private Double totalBCA;
    @Ignore
    private Double totalBRI;
    @Ignore
    private Double totalCash;
    @Ignore
    private Double modalBCA;
    @Ignore
    private Double modalBRI;
    @Ignore
    private Double modalCash;

    @Ignore
    private List<TransactionItem> items;

    public Transaction(Integer id, String type, String customerName, String transactionDate, String paymentType, String status, Double discount, String note, Double subTotal, Double grandTotal, List<TransactionItem> items, Boolean isActive) {
        this.id = id;
        this.type = type;
        this.customerName = customerName;
        this.transactionDate = transactionDate;
        this.paymentType = paymentType;
        this.status = status;
        this.discount = discount;
        this.note = note;
        this.subTotal = subTotal;
        this.grandTotal = grandTotal;
        this.items = items;
        this.isActive = isActive;
    }

    public Transaction(String type, String customerName, String transactionDate, String paymentType, String status, Double discount, String note, Double subTotal, Double grandTotal, Boolean isActive) {
        this.type = type;
        this.customerName = customerName;
        this.transactionDate = transactionDate;
        this.paymentType = paymentType;
        this.status = status;
        this.discount = discount;
        this.note = note;
        this.subTotal = subTotal;
        this.grandTotal = grandTotal;
        this.isActive = isActive;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public Double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public List<TransactionItem> getItems() {
        return items;
    }

    public void setItems(List<TransactionItem> items) {
        this.items = items;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Double getTotalBCA() {
        return totalBCA;
    }

    public void setTotalBCA(Double totalBCA) {
        this.totalBCA = totalBCA;
    }

    public Double getTotalBRI() {
        return totalBRI;
    }

    public void setTotalBRI(Double totalBRI) {
        this.totalBRI = totalBRI;
    }

    public Double getTotalCash() {
        return totalCash;
    }

    public void setTotalCash(Double totalCash) {
        this.totalCash = totalCash;
    }
}
