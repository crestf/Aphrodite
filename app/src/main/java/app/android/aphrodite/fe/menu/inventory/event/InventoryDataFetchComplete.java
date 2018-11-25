package app.android.aphrodite.fe.menu.inventory.event;

import app.android.aphrodite.be.model.TransactionItem;
import app.android.aphrodite.fe.base.BaseEvent;
import app.android.aphrodite.be.model.Inventory;

public class InventoryDataFetchComplete extends BaseEvent {

    private final Boolean success;
    private final String message;
    private final TransactionItem data;

    public InventoryDataFetchComplete(TransactionItem data) {
        this.success = true;
        this.message = null;
        this.data = data;
    }
    public InventoryDataFetchComplete(String message) {
        this.success = false;
        this.message = message;
        this.data = null;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public TransactionItem getData() {
        return data;
    }
}
