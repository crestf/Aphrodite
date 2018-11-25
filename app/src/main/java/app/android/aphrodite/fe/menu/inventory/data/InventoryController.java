package app.android.aphrodite.fe.menu.inventory.data;

import android.annotation.SuppressLint;
import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;

import app.android.aphrodite.be.model.TransactionItem;
import app.android.aphrodite.fe.base.BaseController;
import app.android.aphrodite.fe.base.BaseEvent;
import app.android.aphrodite.fe.common.HelperUtil;
import app.android.aphrodite.fe.common.SharedPrefManager;
import app.android.aphrodite.fe.menu.inventory.event.InventoryDataFetchComplete;
import app.android.aphrodite.fe.menu.inventory.event.InventoryDetailListFetchComplete;
import app.android.aphrodite.fe.menu.inventory.event.InventoryListFetchComplete;
import app.android.aphrodite.fe.menu.inventory.event.InventorySaveComplete;
import app.android.aphrodite.be.model.Inventory;

@SuppressLint("StaticFieldLeak")
public class InventoryController extends BaseController {

    public InventoryController(Context context) {
        super(context);
    }

    public void fetchInventoryList(final String searchString) {
        execute(new AsyncProcess() {
            @Override
            public BaseEvent doInBackground() {
                try {
                    Boolean showInactiveContent = SharedPrefManager.getInstance(context).getShowInactive();
                    String query = "%" + searchString + "%";

                    ArrayList<Inventory> data = new ArrayList<>();
                    if (showInactiveContent) {
                        data.addAll(getDB().inventoryDao().selectAll(query));
                    } else {
                        data.addAll(getDB().inventoryDao().selectAllActive(query));
                    }

                    return new InventoryListFetchComplete(data);
                } catch (Exception e) {
                    return new InventoryListFetchComplete(e.getMessage());
                }
            }

            @Override
            public void onComplete(BaseEvent event) {
                EventBus.getDefault().post(event);
            }
        });
    }

    public void fetchInventoryDetailList(final Integer id) {
        execute(new AsyncProcess() {
            @Override
            public BaseEvent doInBackground() {
                try {
                    Inventory item = getDB().inventoryDao().findById(id);
                    ArrayList<TransactionItem> data = new ArrayList<>();

                    data.addAll(getDB().transactionItemDao().selectAllByItemId(id));
                    return new InventoryDetailListFetchComplete(item, data);
                } catch (Exception e) {
                    return new InventoryDetailListFetchComplete(e.getMessage());
                }
            }

            @Override
            public void onComplete(BaseEvent event) {
                EventBus.getDefault().post(event);
            }
        });
    }

    public void fetchInventoryData(final Integer itemId) {
        execute(new AsyncProcess() {
            @Override
            public BaseEvent doInBackground() {
                try {
                    Inventory item = getDB().inventoryDao().findById(itemId);
                    TransactionItem data = new TransactionItem(null, null,
                            HelperUtil.formatDateToDB(new Date()), itemId,
                            item.getName(), item.getHargaBeli(), item.getHargaJual(), null, null);
                    return new InventoryDataFetchComplete(data);
                } catch (Exception e) {
                    return new InventoryDataFetchComplete(e.getMessage());
                }
            }

            @Override
            public void onComplete(BaseEvent event) {
                EventBus.getDefault().post(event);
            }
        });
    }

    public void fetchInventoryTransactionData(final Integer id) {
        execute(new AsyncProcess() {
            @Override
            public BaseEvent doInBackground() {
                try {
                    TransactionItem item = getDB().transactionItemDao().findById(id);
                    return new InventoryDataFetchComplete(item);
                } catch (Exception e) {
                    return new InventoryDataFetchComplete(e.getMessage());
                }
            }

            @Override
            public void onComplete(BaseEvent event) {
                EventBus.getDefault().post(event);
            }
        });
    }

    public void addInventory(final TransactionItem data) {
        execute(new AsyncProcess() {
            @Override
            public BaseEvent doInBackground() {
                getDB().beginTransaction();
                try {
                    Integer itemId;
                    Inventory item = getDB().inventoryDao().findByProps(data.getName(), data.getHargaBeli(), data.getHargaJual());
                    if (item == null) {
                        itemId = createItem(data.getName(), data.getHargaBeli(), data.getHargaJual(), data.getQuantity());
                    } else {
                        itemId = item.getId();
                    }

                    data.setItemId(itemId);
                    getDB().transactionItemDao().save(data);

                    if (item != null) {
                        item.setQuantity(item.getQuantity() + data.getQuantity());
                        getDB().inventoryDao().save(item);
                    }

                    getDB().setTransactionSuccessful();
                    getDB().endTransaction();

                    return new InventorySaveComplete(true, "Data Saved");
                } catch (Exception e) {
                    getDB().endTransaction();
                    return new InventorySaveComplete(false, e.getMessage());
                }
            }

            @Override
            public void onComplete(BaseEvent event) {
                EventBus.getDefault().post(event);
            }
        });
    }

    public void updateInventory(final TransactionItem data) {
        execute(new AsyncProcess() {
            @Override
            public BaseEvent doInBackground() {
                getDB().beginTransaction();
                try {
                    Inventory currentItem = getDB().inventoryDao().findById(data.getItemId());
                    TransactionItem currentTrans = getDB().transactionItemDao().findById(data.getId());
                    if (currentItem == null || currentTrans == null) {
                        throw new Exception("Data not found");
                    }

                    Double updatedQuantity = currentItem.getQuantity();
                    if (currentTrans.getActive()) {
                        updatedQuantity = updatedQuantity - currentTrans.getQuantity();
                    }
                    if (data.getActive()) {
                        updatedQuantity = updatedQuantity + data.getQuantity();
                    }

                    if (updatedQuantity < 0) {
                        throw new Exception("Cannot update data. Stock not enough");
                    }

                    getDB().transactionItemDao().save(data);

                    currentItem.setQuantity(updatedQuantity);
                    getDB().inventoryDao().save(currentItem);

                    getDB().setTransactionSuccessful();
                    getDB().endTransaction();

                    return new InventorySaveComplete(true, "Data Saved");
                } catch (Exception e) {
                    getDB().endTransaction();
                    return new InventorySaveComplete(false, e.getMessage());
                }
            }

            @Override
            public void onComplete(BaseEvent event) {
                EventBus.getDefault().post(event);
            }
        });
    }

    private Integer createItem(String name, Double hargaBeli, Double hargaJual, Double quantity) {
        Inventory item = new Inventory(null, name, hargaBeli, hargaJual, quantity, true);
        return getDB().inventoryDao().save(item).intValue();
    }

    public void fetchAvailableInventory(final String searchString) {
        execute(new AsyncProcess() {
            @Override
            public BaseEvent doInBackground() {
                try {
                    String query = "%" + searchString + "%";

                    ArrayList<Inventory> data = new ArrayList<>();
                    data.addAll(getDB().inventoryDao().selectAllAvailable(query));

                    return new InventoryListFetchComplete(data);
                } catch (Exception e) {
                    return new InventoryListFetchComplete(e.getMessage());
                }
            }

            @Override
            public void onComplete(BaseEvent event) {
                EventBus.getDefault().post(event);
            }
        });
    }
}
