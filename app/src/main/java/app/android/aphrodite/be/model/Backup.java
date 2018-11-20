package app.android.aphrodite.be.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

public class Backup {

    List<Inventory> listInventory;
    List<Transaction> listTransaction;
    List<TransactionItem> listTransactionItem;

    public Backup(List<Inventory> listInventory, List<Transaction> listTransaction, List<TransactionItem> listTransactionItem) {
        this.listInventory = listInventory;
        this.listTransaction = listTransaction;
        this.listTransactionItem = listTransactionItem;
    }

    public List<Inventory> getListInventory() {
        return listInventory;
    }

    public void setListInventory(List<Inventory> listInventory) {
        this.listInventory = listInventory;
    }

    public List<Transaction> getListTransaction() {
        return listTransaction;
    }

    public void setListTransaction(List<Transaction> listTransaction) {
        this.listTransaction = listTransaction;
    }

    public List<TransactionItem> getListTransactionItem() {
        return listTransactionItem;
    }

    public void setListTransactionItem(List<TransactionItem> listTransactionItem) {
        this.listTransactionItem = listTransactionItem;
    }
}
