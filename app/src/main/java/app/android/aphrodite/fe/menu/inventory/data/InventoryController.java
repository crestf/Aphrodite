package app.android.aphrodite.fe.menu.inventory.data;

import android.annotation.SuppressLint;
import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import app.android.aphrodite.be.enums.TransactionTypeEnum;
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
//                    String startDate = HelperUtil.formatDisplayToDB(SharedPrefManager.getInstance(context).getStartDate());
//                    String endDate = HelperUtil.formatDisplayToDB(SharedPrefManager.getInstance(context).getEndDate());
                    Boolean showInactiveContent = SharedPrefManager.getInstance(context).getShowInactive();
                    String query = "%" + searchString + "%";

                    ArrayList<Inventory> data = new ArrayList<>();
                    if (showInactiveContent) {
                        data.addAll(getDB().inventoryDao().selectAll(query));
//                        data.addAll(getDB().inventoryDao().selectAll(query, startDate, endDate));
                    } else {
                        data.addAll(getDB().inventoryDao().selectAllActive(query));
//                        data.addAll(getDB().inventoryDao().selectAllActive(query, startDate, endDate));
                    }

                    for (Inventory item : data) {
                        Double usage = getDB().transactionItemDao().findUsageByProps(item.getName(), item.getSellPrice(),
                                item.getCapitalPrice(), TransactionTypeEnum.READY_STOCK);
                        if (usage == null)
                            usage = 0d;
                        item.setQuantity(item.getQuantity() - usage);
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
                Inventory item = getDB().inventoryDao().findById(id);
                try {
                    ArrayList<Inventory> data = new ArrayList<>();
                    data.addAll(getDB().inventoryDao().getSameItemInventory(item.getName(), item.getCapitalPrice(), item.getSellPrice()));

                    for (Inventory d : data) {
                        d.setTransaction(false);
                    }

                    ArrayList<TransactionItem> trans = new ArrayList<>();
                    trans.addAll(getDB().transactionItemDao().getSameTransactionItem(item.getName(), item.getCapitalPrice(), item.getSellPrice(),
                            TransactionTypeEnum.READY_STOCK));

                    for (TransactionItem transItem : trans) {
                        Inventory iv = new Inventory(-1, transItem.getTransactionDate(), transItem.getName(), transItem.getHargaBeli(), transItem.getHargaJual(),
                                transItem.getQuantity(), transItem.getActive());
                        iv.setTransaction(true);
                        data.add(iv);
                    }

                    Collections.sort(data, new Comparator<Inventory>() {
                        @Override
                        public int compare(Inventory o1, Inventory o2) {
                            return o1.getDate().compareTo(o2.getDate());
                        }
                    });

                    return new InventoryDetailListFetchComplete(item, data);
                } catch (Exception e) {
                    return new InventoryDetailListFetchComplete(item, e.getMessage());
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
                    item.setId(null);
                    item.setQuantity(null);
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

    public void fetchInventoryTransactionData(final Integer id) {
        execute(new AsyncProcess() {
            @Override
            public BaseEvent doInBackground() {
                try {
                    Inventory item = getDB().inventoryDao().findById(id);
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

    public void saveInventory(final Inventory data) {
        execute(new AsyncProcess() {
            @Override
            public BaseEvent doInBackground() {
                getDB().beginTransaction();
                try {
                    getDB().inventoryDao().save(data);

                    if (data.getActive() && findAvailableStock(data.getName(), data.getSellPrice(), data.getCapitalPrice()) < 0) {
                        throw new Exception("Cannot save data due to invalid stock (<0)");
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

    public void fetchAvailableInventory(final String searchString) {
        execute(new AsyncProcess() {
            @Override
            public BaseEvent doInBackground() {
                try {
                    String query = "%" + searchString + "%";

                    ArrayList<Inventory> data = new ArrayList<>();
                    data.addAll(getDB().inventoryDao().selectAllAvailable(query));

                    for (Inventory item : data) {
                        Double usage = getDB().transactionItemDao().findUsageByProps(item.getName(), item.getSellPrice(),
                                item.getCapitalPrice(), TransactionTypeEnum.READY_STOCK);
                        if (usage == null)
                            usage = 0d;
                        item.setQuantity(item.getQuantity() - usage);
                    }

                    ArrayList<Inventory> result = new ArrayList<>();
                    for (Inventory item : data) {
                        if (item.getQuantity() > 0) {
                            result.add(item);
                        }
                    }

                    return new InventoryListFetchComplete(result);
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
