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

    @Query("SELECT i.id, i.name, i.sellPrice, i.capitalPrice, SUM(i.quantity) as quantity, i.isActive " +
            "FROM inventory i " +
            "WHERE i.date BETWEEN :startDate AND :endDate " +
            "AND name like :query " +
            "GROUP BY i.name, i.capitalPrice, i.sellPrice " +
            "ORDER BY i.id DESC")
    List<Inventory> selectAll(String query, String startDate, String endDate);

    @Query("SELECT i.id, i.name, i.sellPrice, i.capitalPrice, SUM(i.quantity) as quantity, i.isActive " +
            "FROM inventory i " +
            "WHERE i.date BETWEEN :startDate AND :endDate " +
            "AND name like :query " +
            "AND isActive=1 " +
            "GROUP BY i.name, i.capitalPrice, i.sellPrice " +
            "ORDER BY i.id DESC")
    List<Inventory> selectAllActive(String query, String startDate, String endDate);

    @Query("SELECT i.id, i.name, i.sellPrice, i.capitalPrice, SUM(i.quantity) as quantity, i.isActive " +
            "FROM inventory i " +
            "WHERE name like :query " +
            "GROUP BY i.name, i.capitalPrice, i.sellPrice " +
            "ORDER BY i.id DESC")
    List<Inventory> selectAll(String query);

    @Query("SELECT i.id, i.name, i.sellPrice, i.capitalPrice, SUM(i.quantity) as quantity, i.isActive " +
            "FROM inventory i " +
            "WHERE name like :query " +
            "AND isActive=1 " +
            "GROUP BY i.name, i.capitalPrice, i.sellPrice " +
            "ORDER BY i.id DESC")
    List<Inventory> selectAllActive(String query);

    @Query("SELECT * from inventory " +
            "WHERE name=:itemName " +
            "AND capitalPrice=:capitalPrice " +
            "AND sellPrice=:sellPrice " +
            "ORDER BY id DESC")
    List<Inventory> getSameItemInventory(String itemName, Double capitalPrice, Double sellPrice);

    @Query("SELECT i.id, i.name, i.sellPrice, i.capitalPrice ,SUM(i.quantity) as quantity, i.isActive " +
            "FROM inventory i " +
            "WHERE name like :query " +
            "AND quantity > 0 " +
            "AND isActive=1 " +
            "GROUP BY i.name, i.capitalPrice, i.sellPrice " +
            "ORDER BY i.id DESC")
    List<Inventory> selectAllAvailable(String query);

    @Query("SELECT * FROM inventory WHERE id=:id LIMIT 1")
    Inventory findById(Integer id);

    @Query("SELECT SUM(quantity) as quantity " +
            "FROM inventory " +
            "WHERE name=:name " +
            "AND sellPrice=:sellPrice " +
            "AND capitalPrice=:capitalPrice " +
            "AND isActive=1 ")
    Double findStockByProps(String name, Double sellPrice, Double capitalPrice);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(Inventory data);

    @Delete
    void delete(Inventory data);

    @Query("DELETE FROM inventory")
    void deleteAll();
}
