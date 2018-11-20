package app.android.aphrodite.be.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Inventory {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "capitalPrice")
    private Double capitalPrice;

    @ColumnInfo(name = "sellPrice")
    private Double sellPrice;

    @ColumnInfo(name = "quantity")
    private Double quantity;

    @ColumnInfo(name = "isActive")
    private Boolean isActive;

    @Ignore
    private Boolean isTransaction;

    public Inventory(int id, String date, String name, Double capitalPrice, Double sellPrice, Double quantity, Boolean isActive) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.capitalPrice = capitalPrice;
        this.sellPrice = sellPrice;
        this.quantity = quantity;
        this.isActive = isActive;
    }
    @Ignore
    public Inventory(String date, String name, Double capitalPrice, Double sellPrice, Double quantity, Boolean isActive) {
        this.date = date;
        this.name = name;
        this.capitalPrice = capitalPrice;
        this.sellPrice = sellPrice;
        this.quantity = quantity;
        this.isActive = isActive;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCapitalPrice() {
        return capitalPrice;
    }

    public void setCapitalPrice(Double capitalPrice) {
        this.capitalPrice = capitalPrice;
    }

    public Double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(Double sellPrice) {
        this.sellPrice = sellPrice;
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

    public Boolean getTransaction() {
        return isTransaction;
    }

    public void setTransaction(Boolean transaction) {
        isTransaction = transaction;
    }
}
