package app.android.aphrodite.be.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import app.android.aphrodite.be.model.Inventory;
import app.android.aphrodite.be.model.Transaction;
import app.android.aphrodite.be.model.TransactionItem;

@Database(
        entities = {
                Inventory.class,
                Transaction.class,
                TransactionItem.class
        },
        version = 1
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract InventoryDao inventoryDao();
    public abstract TransactionDao transactionDao();
    public abstract TransactionItemDao transactionItemDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "app_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
