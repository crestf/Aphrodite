package app.android.aphrodite.be.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import app.android.aphrodite.be.model.Inventory;
import app.android.aphrodite.be.model.Recap;
import app.android.aphrodite.be.model.Transaction;

@Dao
public interface TransactionDao {

    @Query("SELECT * " +
            "FROM `transaction`")
    List<Transaction> selectAll();

    @Query("SELECT * " +
            "FROM `transaction` " +
            "WHERE transactionDate BETWEEN :startDate AND :endDate " +
            "AND isActive=1 " +
            "ORDER BY id DESC")
    List<Transaction> selectAllActive(String startDate, String endDate);

    @Query("SELECT * " +
            "FROM `transaction` " +
            "WHERE transactionDate BETWEEN :startDate AND :endDate " +
            "ORDER BY id DESC")
    List<Transaction> selectAll(String startDate, String endDate);

    @Query("SELECT * " +
            "FROM `transaction` " +
            "WHERE transactionDate BETWEEN :startDate AND :endDate " +
            "AND status=:status " +
            "AND isActive=1 " +
            "ORDER BY id DESC")
    List<Transaction> selectAllActiveByStatus(String startDate, String endDate, String status);

    @Query("SELECT * " +
            "FROM `transaction` " +
            "WHERE transactionDate BETWEEN :startDate AND :endDate " +
            "AND status=:status " +
            "ORDER BY id DESC")
    List<Transaction> selectAllByStatus(String startDate, String endDate, String status);

    @Query("SELECT * FROM `transaction` WHERE id=:id LIMIT 1")
    Transaction findById(Integer id);


    @Query("SELECT SUM(grandTotal) " +
            "FROM `transaction` " +
            "WHERE customerName=:customerName " +
            "AND transactionDate BETWEEN :startDate AND :endDate " +
            "AND paymentType=:paymentType " +
            "AND isActive = 1")
    Double getRecapTotal(String customerName, String startDate, String endDate, String paymentType);

    @Query("SELECT DISTINCT customerName as name " +
            "FROM `transaction` " +
            "ORDER BY id DESC")
    List<Recap> getCustomerList();

    @Query("SELECT h.customerName as name, SUM(d1.hargaBeli * d1.quantity) as modal, SUM(d1.hargaJual * d1.quantity) as total " +
            "FROM `transaction` h " +
            "INNER JOIN transactionitem d1 " +
            "ON h.id = d1.headerId " +
            "AND h.paymentType=:paymentType " +
            "AND d1.isActive=1 " +
            "WHERE h.transactionDate BETWEEN :startDate AND :endDate " +
            "AND h.isActive = 1 " +
            "AND h.customerName=:name " +
            "GROUP BY h.customerName")
    Recap getRecap(String name, String startDate, String endDate, String paymentType);

    @Query("SELECT SUM(discount) " +
            "FROM `transaction` " +
            "WHERE transactionDate BETWEEN :startDate AND :endDate " +
            "AND paymentType=:paymentType " +
            "AND isActive = 1 " +
            "AND customerName=:name " +
            "GROUP BY customerName")
    Double getTotalDiscount(String name, String startDate, String endDate, String paymentType);

    @Query("SELECT COUNT(id) " +
            "FROM `transaction` " +
            "WHERE transactionDate BETWEEN :startDate AND :endDate " +
            "AND isActive = 1 " +
            "AND customerName = :name " +
            "GROUP BY customerName")
    Double getTotalQty(String name, String startDate, String endDate);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long save(Transaction data);

    @Delete
    void delete(Transaction data);

    @Query("DELETE FROM `transaction`")
    void deleteAll();

}
