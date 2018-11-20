package app.android.aphrodite.fe.menu.transaction.event;

import java.util.ArrayList;
import java.util.List;

import app.android.aphrodite.be.model.Recap;
import app.android.aphrodite.be.model.Transaction;
import app.android.aphrodite.fe.base.BaseEvent;
import app.android.aphrodite.be.model.TransactionItem;

public class RecapitulationListFetchComplete extends BaseEvent {

    private final Boolean success;
    private final String message;
    private final List<Recap> data;

    public RecapitulationListFetchComplete(List<Recap> data) {
        this.success = true;
        this.message = null;
        this.data = data;
    }
    public RecapitulationListFetchComplete(String message) {
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

    public List<Recap> getData() {
        return data;
    }
}
