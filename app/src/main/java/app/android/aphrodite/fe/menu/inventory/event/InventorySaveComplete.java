package app.android.aphrodite.fe.menu.inventory.event;

import app.android.aphrodite.fe.base.BaseEvent;

public class InventorySaveComplete extends BaseEvent {

    private final Boolean success;
    private final String message;

    public InventorySaveComplete(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

}
