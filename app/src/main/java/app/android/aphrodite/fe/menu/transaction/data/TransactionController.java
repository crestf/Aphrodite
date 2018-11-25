package app.android.aphrodite.fe.menu.transaction.data;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import app.android.aphrodite.be.enums.TransactionTypeEnum;
import app.android.aphrodite.be.model.Inventory;
import app.android.aphrodite.fe.base.BaseController;
import app.android.aphrodite.fe.base.BaseEvent;
import app.android.aphrodite.fe.common.HelperUtil;
import app.android.aphrodite.fe.common.SharedPrefManager;
import app.android.aphrodite.be.enums.TransactionStatusEnum;
import app.android.aphrodite.fe.menu.transaction.event.TransactionDataFetchComplete;
import app.android.aphrodite.fe.menu.transaction.event.TransactionListFetchComplete;
import app.android.aphrodite.fe.menu.transaction.event.TransactionSaveComplete;
import app.android.aphrodite.be.model.Transaction;
import app.android.aphrodite.be.model.TransactionItem;

public class TransactionController extends BaseController {

    public TransactionController(Context context) {
        super(context);
    }

    public void fetchTransactionList(final String status) {
        execute(new AsyncProcess() {
            @Override
            public BaseEvent doInBackground() {
                try {
                    String startDate = HelperUtil.formatDisplayToDB(SharedPrefManager.getInstance(context).getStartDate());
                    String endDate = HelperUtil.formatDisplayToDB(SharedPrefManager.getInstance(context).getEndDate());
                    Boolean showInactiveContent = SharedPrefManager.getInstance(context).getShowInactive();

                    ArrayList<Transaction> data = new ArrayList<>();
                    if (status.equalsIgnoreCase(TransactionStatusEnum.ALL)) {
                        data.addAll(showInactiveContent ?
                                        getDB().transactionDao().selectAll(startDate, endDate) :
                                        getDB().transactionDao().selectAllActive(startDate, endDate));
                    } else {
                        data.addAll(showInactiveContent ?
                                        getDB().transactionDao().selectAllByStatus(startDate, endDate, status) :
                                        getDB().transactionDao().selectAllActiveByStatus(startDate, endDate, status));
                    }

                    return new TransactionListFetchComplete(data);
                } catch (Exception e) {
                    return new TransactionListFetchComplete(e.getMessage());
                }
            }

            @Override
            public void onComplete(BaseEvent event) {
                EventBus.getDefault().post(event);
            }
        });
    }

    public void fetchTransactionData(final Integer id) {
        execute(new AsyncProcess() {
            @Override
            public BaseEvent doInBackground() {
                try {
                    Transaction header = getDB().transactionDao().findById(id);
                    List<TransactionItem> detail = getDB().transactionItemDao().selectAllByHeaderId(id);

                    return new TransactionDataFetchComplete(header, detail);
                } catch (Exception e) {
                    return new TransactionDataFetchComplete(e.getMessage());
                }
            }

            @Override
            public void onComplete(BaseEvent event) {
                EventBus.getDefault().post(event);
            }
        });
    }

    public void addTransaction(final Transaction header, final List<TransactionItem> details) {
        execute(new AsyncProcess() {
            @Override
            public BaseEvent doInBackground() {
                getDB().beginTransaction();
                try {
                    saveHeaderAndNewDetail(header, details);

                    getDB().setTransactionSuccessful();
                    getDB().endTransaction();

                    return new TransactionSaveComplete(true, null);
                } catch (Exception e) {
                    getDB().endTransaction();
                    return new TransactionSaveComplete(false, e.getMessage());
                }
            }

            @Override
            public void onComplete(BaseEvent event) {
                EventBus.getDefault().post(event);
            }
        });
    }

    private void saveHeaderAndNewDetail(Transaction header, List<TransactionItem> details) throws Exception {
        header.setGrandTotal(header.getSubTotal() - header.getDiscount());
        Integer id = getDB().transactionDao().save(header).intValue();
        Boolean isReadyStock = header.getType().equalsIgnoreCase(TransactionTypeEnum.READY_STOCK);

        for (TransactionItem item : details) {
            if (isReadyStock) {
                // Check if stock valid
                Inventory inventory = getDB().inventoryDao().findById(item.getItemId());

                Double newQuantity = inventory.getQuantity();
                if (header.getActive()) {
                    newQuantity = newQuantity - item.getQuantity();
                }
                if (newQuantity < 0) {
                    throw new Exception("Stock for item " + item.getName() + " is not enough");
                }
                inventory.setQuantity(newQuantity);
                getDB().inventoryDao().save(inventory);
            }

            item.setHeaderId(id);
            item.setTransactionDate(header.getTransactionDate());
            item.setActive(header.getActive());
            getDB().transactionItemDao().save(item);
        }
    }

    public void updateTransaction(final Transaction header, final List<TransactionItem> details) {
        execute(new AsyncProcess() {
            @Override
            public BaseEvent doInBackground() {
                getDB().beginTransaction();
                try {
                    // Reset dulu current transaction item
                    List<TransactionItem> currentTransactions = getDB().transactionItemDao().selectAllByHeaderId(header.getId());

                    for (TransactionItem item : currentTransactions) {
                        // Reverse stock
                        Inventory inventory = getDB().inventoryDao().findById(item.getItemId());

                        Double newQuantity = inventory.getQuantity();
                        if (item.getActive()) {
                            newQuantity = newQuantity + item.getQuantity();
                        }
                        inventory.setQuantity(newQuantity);
                        getDB().inventoryDao().save(inventory);

                        getDB().transactionItemDao().delete(item);
                    }

                    saveHeaderAndNewDetail(header, details);

                    getDB().setTransactionSuccessful();
                    getDB().endTransaction();

                    return new TransactionSaveComplete(true, null);
                } catch (Exception e) {
                    getDB().endTransaction();
                    return new TransactionSaveComplete(false, e.getMessage());
                }
            }

            @Override
            public void onComplete(BaseEvent event) {
                EventBus.getDefault().post(event);
            }
        });
    }
}
