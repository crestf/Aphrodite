package app.android.aphrodite.be.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import app.android.aphrodite.be.model.Inventory;
import app.android.aphrodite.be.model.TransactionItem;

@Dao
public interface TransactionItemDao {

    @Query("SELECT * " +
            "FROM TransactionItem")
    List<TransactionItem> selectAll();


    @Query("SELECT * " +
            "FROM TransactionItem " +
            "WHERE headerId=:id ")
    List<TransactionItem> findByHeaderId(Integer id);

    @Query("SELECT * FROM transactionitem WHERE id=:id AND isActive=1 LIMIT 1")
    TransactionItem findById(Integer id);

    @Query("SELECT SUM(quantity) as quantity " +
            "FROM TransactionItem " +
            "WHERE name=:name " +
            "AND hargaJual=:sellPrice " +
            "AND hargaBeli=:capitalPrice " +
            "AND isActive=1 ")
    Double findUsageByProps(String name, Double sellPrice, Double capitalPrice);

    @Query("SELECT * from transactionitem " +
            "WHERE name=:itemName " +
            "AND hargaBeli=:capitalPrice " +
            "AND hargaJual=:sellPrice " +
            "ORDER BY id DESC")
    List<TransactionItem> getSameTransactionItem(String itemName, Double capitalPrice, Double sellPrice);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(TransactionItem data);

    @Delete
    void delete(TransactionItem data);

    @Query("DELETE FROM transactionitem")
    void deleteAll();

    @Query("DELETE FROM transactionitem WHERE headerId=:id")
    void deleteByHeaderId(Long id);
}
