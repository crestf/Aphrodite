package app.android.aphrodite.fe.menu.inventory.event;

import java.util.ArrayList;

import app.android.aphrodite.fe.base.BaseEvent;
import app.android.aphrodite.be.model.Inventory;

public class InventoryListFetchComplete extends BaseEvent {

    private final Boolean success;
    private final String message;
    private final ArrayList<Inventory> data;

    public InventoryListFetchComplete(ArrayList<Inventory> data) {
        this.success = true;
        this.message = null;
        this.data = data;
    }
    public InventoryListFetchComplete(String message) {
        this.success = false;
        this.message = message;
        this.data = new ArrayList<>();
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<Inventory> getData() {
        return data;
    }
}
