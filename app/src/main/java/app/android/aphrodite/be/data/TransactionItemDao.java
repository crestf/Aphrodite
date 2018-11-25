package app.android.aphrodite.be.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.Collection;
import java.util.List;

import app.android.aphrodite.be.enums.TransactionTypeEnum;
import app.android.aphrodite.be.model.Inventory;
import app.android.aphrodite.be.model.TransactionItem;

@Dao
public interface TransactionItemDao {

    @Query("SELECT * " +
            "FROM TransactionItem")
    List<TransactionItem> selectAll();

    @Query("SELECT * FROM transactionitem " +
            "WHERE itemId = :id " +
            "ORDER BY transactionDate DESC")
    List<TransactionItem> selectAllByItemId(Integer id);

    @Query("SELECT * " +
            "FROM TransactionItem " +
            "WHERE headerId = :id ")
    List<TransactionItem> selectAllByHeaderId(Integer id);



    @Query("SELECT * FROM transactionitem WHERE id=:id")
    TransactionItem findById(Integer id);



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long save(TransactionItem data);

    @Delete
    void delete(TransactionItem data);

    @Query("DELETE FROM transactionitem")
    void deleteAll();
}
