package app.android.aphrodite.be.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class TransactionItem {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @ColumnInfo(name = "headerId")
    private Integer headerId;

    @ColumnInfo(name = "transactionDate")
    private String transactionDate;

    @ColumnInfo(name = "itemId")
    private Integer itemId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "hargaBeli")
    private Double hargaBeli;

    @ColumnInfo(name = "hargaJual")
    private Double hargaJual;

    @ColumnInfo(name = "quantity")
    private Double quantity;

    @ColumnInfo(name = "isActive")
    private Boolean isActive;

    public TransactionItem(Integer id, Integer headerId, String transactionDate, Integer itemId, String name, Double hargaBeli, Double hargaJual, Double quantity, Boolean isActive) {
        this.id = id;
        this.headerId = headerId;
        this.transactionDate = transactionDate;
        this.itemId = itemId;
        this.name = name;
        this.hargaBeli = hargaBeli;
        this.hargaJual = hargaJual;
        this.quantity = quantity;
        this.isActive = isActive;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getHeaderId() {
        return headerId;
    }

    public void setHeaderId(Integer headerId) {
        this.headerId = headerId;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getHargaBeli() {
        return hargaBeli;
    }

    public void setHargaBeli(Double hargaBeli) {
        this.hargaBeli = hargaBeli;
    }

    public Double getHargaJual() {
        return hargaJual;
    }

    public void setHargaJual(Double hargaJual) {
        this.hargaJual = hargaJual;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
