package app.android.aphrodite.fe.menu.transaction.data;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import app.android.aphrodite.be.enums.TransactionTypeEnum;
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
                    List<TransactionItem> detail = getDB().transactionItemDao().findByHeaderId(id);

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

    public void saveTransaction(final Transaction header, final List<TransactionItem> details) {
        execute(new AsyncProcess() {
            @Override
            public BaseEvent doInBackground() {
                getDB().beginTransaction();
                try {
                    header.setGrandTotal(header.getSubTotal() - header.getDiscount());
                    Long id = getDB().transactionDao().save(header);
                    Boolean isReadyStock = header.getType().equalsIgnoreCase(TransactionTypeEnum.READY_STOCK);

                    getDB().transactionItemDao().deleteByHeaderId(id);

                    for (TransactionItem item : details) {
                        if (isReadyStock) {
                            // Check if stock valid
                            Double stock = findAvailableStock(item.getName(), item.getHargaJual(), item.getHargaBeli());
                            if (stock < item.getQuantity()) {
                                throw new Exception("Stock for item " + item.getName() + " is not enough");
                            }
                        }

                        item.setHeaderId(id.intValue());
                        item.setTransactionDate(header.getTransactionDate());
                        item.setActive(header.getActive());
                        getDB().transactionItemDao().save(item);
                    }

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
