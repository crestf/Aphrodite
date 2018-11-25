package app.android.aphrodite.be.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import app.android.aphrodite.be.model.Inventory;

@Dao
public interface InventoryDao {

    @Query("SELECT * " +
            "FROM inventory")
    List<Inventory> selectAll();



    @Query("SELECT * " +
            "FROM inventory " +
            "WHERE name like :query " +
            "ORDER BY id DESC")
    List<Inventory> selectAll(String query);

    @Query("SELECT * " +
            "FROM inventory " +
            "WHERE name like :query " +
            "AND isActive = 1 " +
            "ORDER BY id DESC")
    List<Inventory> selectAllActive(String query);

    @Query("SELECT * " +
            "FROM inventory " +
            "WHERE name like :query " +
            "AND isActive = 1 " +
            "AND quantity > 0 " +
            "ORDER BY id DESC")
    List<Inventory> selectAllAvailable(String query);



    @Query("SELECT * " +
            "FROM inventory " +
            "WHERE id = :id")
    Inventory findById(Integer id);

    @Query("SELECT * FROM Inventory " +
            "WHERE name=:name " +
            "AND hargaBeli=:hargaBeli " +
            "AND hargaJual=:hargaJual ")
    Inventory findByProps(String name, Double hargaBeli, Double hargaJual);



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long save(Inventory data);

    @Delete
    void delete(Inventory data);

    @Query("DELETE FROM inventory")
    void deleteAll();

}
