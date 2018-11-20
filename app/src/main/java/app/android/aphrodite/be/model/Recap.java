package app.android.aphrodite.be.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

public class Recap {

    public String name;
    public Double qty;
    public Double modalBCA;
    public Double totalBCA;
    public Double modalBRI;
    public Double totalBRI;
    public Double modalCash;
    public Double totalCash;
    public Double modal;
    public Double total;

    public Double getModal() {
        return modal;
    }

    public void setModal(Double modal) {
        this.modal = modal;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Recap() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public Double getModalBCA() {
        return modalBCA;
    }

    public void setModalBCA(Double modalBCA) {
        this.modalBCA = modalBCA;
    }

    public Double getTotalBCA() {
        return totalBCA;
    }

    public void setTotalBCA(Double totalBCA) {
        this.totalBCA = totalBCA;
    }

    public Double getModalBRI() {
        return modalBRI;
    }

    public void setModalBRI(Double modalBRI) {
        this.modalBRI = modalBRI;
    }

    public Double getTotalBRI() {
        return totalBRI;
    }

    public void setTotalBRI(Double totalBRI) {
        this.totalBRI = totalBRI;
    }

    public Double getModalCash() {
        return modalCash;
    }

    public void setModalCash(Double modalCash) {
        this.modalCash = modalCash;
    }

    public Double getTotalCash() {
        return totalCash;
    }

    public void setTotalCash(Double totalCash) {
        this.totalCash = totalCash;
    }
}
