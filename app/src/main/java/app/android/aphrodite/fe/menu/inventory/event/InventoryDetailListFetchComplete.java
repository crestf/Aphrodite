package app.android.aphrodite.fe.menu.inventory.event;

import java.util.ArrayList;

import app.android.aphrodite.be.model.TransactionItem;
import app.android.aphrodite.fe.base.BaseEvent;
import app.android.aphrodite.be.model.Inventory;

public class InventoryDetailListFetchComplete extends BaseEvent {

    private final Boolean success;
    private final Inventory item;
    private final String message;
    private final ArrayList<TransactionItem> data;

    public InventoryDetailListFetchComplete(Inventory item, ArrayList<TransactionItem> data) {
        this.success = true;
        this.item = item;
        this.message = null;
        this.data = data;
    }
    public InventoryDetailListFetchComplete(String message) {
        this.success = false;
        this.item = null;
        this.message = message;
        this.data = new ArrayList<>();
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<TransactionItem> getData() {
        return data;
    }

    public Inventory getItem() {
        return item;
    }
}
