package app.android.aphrodite.fe.menu.inventory.event;

import java.util.ArrayList;

import app.android.aphrodite.fe.base.BaseEvent;
import app.android.aphrodite.be.model.Inventory;

public class InventoryDetailListFetchComplete extends BaseEvent {

    private final Boolean success;
    private final Inventory item;
    private final String message;
    private final ArrayList<Inventory> data;

    public InventoryDetailListFetchComplete(Inventory item, ArrayList<Inventory> data) {
        this.success = true;
        this.item = item;
        this.message = null;
        this.data = data;
    }
    public InventoryDetailListFetchComplete(Inventory item, String message) {
        this.success = false;
        this.item = item;
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

    public Inventory getItem() {
        return item;
    }
}
