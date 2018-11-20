package app.android.aphrodite.fe.menu.transaction.event;

import app.android.aphrodite.fe.base.BaseEvent;

public class TransactionSaveComplete extends BaseEvent {

    private final Boolean success;
    private final String message;

    public TransactionSaveComplete(Boolean success, String message) {
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
