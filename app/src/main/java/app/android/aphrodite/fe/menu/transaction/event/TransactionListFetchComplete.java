package app.android.aphrodite.fe.menu.transaction.event;


import java.util.ArrayList;

import app.android.aphrodite.fe.base.BaseEvent;
import app.android.aphrodite.be.model.Transaction;

public class TransactionListFetchComplete extends BaseEvent {

    private final Boolean success;
    private final String message;
    private final ArrayList<Transaction> data;

    public TransactionListFetchComplete(ArrayList<Transaction> data) {
        this.success = true;
        this.message = null;
        this.data = data;
    }
    public TransactionListFetchComplete(String message) {
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

    public ArrayList<Transaction> getData() {
        return data;
    }
}
