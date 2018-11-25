package app.android.aphrodite.be.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import app.android.aphrodite.be.enums.TransactionTypeEnum;
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

    @Query("SELECT SUM(i.quantity) as quantity " +
            "FROM TransactionItem i " +
            "INNER JOIN `transaction` h " +
            "ON h.id = i.headerId " +
            "AND h.type = :type " +
            "WHERE i.name=:name " +
            "AND i.hargaJual=:sellPrice " +
            "AND i.hargaBeli=:capitalPrice " +
            "AND i.isActive=1 ")
    Double findUsageByProps(String name, Double sellPrice, Double capitalPrice, String type);

    @Query("SELECT i.* from transactionitem i " +
            "INNER JOIN `transaction` h " +
            "ON h.id = i.headerId " +
            "AND h.type=:type " +
            "WHERE i.name=:itemName " +
            "AND i.hargaBeli=:capitalPrice " +
            "AND i.hargaJual=:sellPrice " +
            "ORDER BY i.id DESC")
    List<TransactionItem> getSameTransactionItem(String itemName, Double capitalPrice, Double sellPrice, String type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(TransactionItem data);

    @Delete
    void delete(TransactionItem data);

    @Query("DELETE FROM transactionitem")
    void deleteAll();

    @Query("DELETE FROM transactionitem WHERE headerId=:id")
    void deleteByHeaderId(Long id);
}
