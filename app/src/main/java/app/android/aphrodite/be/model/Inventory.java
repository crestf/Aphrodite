package app.android.aphrodite.be.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Inventory {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

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

    public Inventory(Integer id, String name, Double hargaBeli, Double hargaJual, Double quantity, Boolean isActive) {
        this.id = id;
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
