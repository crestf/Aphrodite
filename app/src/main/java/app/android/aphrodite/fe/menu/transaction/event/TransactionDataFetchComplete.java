package app.android.aphrodite.fe.menu.transaction.event;


import java.util.ArrayList;
import java.util.List;

import app.android.aphrodite.be.model.Transaction;
import app.android.aphrodite.be.model.TransactionItem;
import app.android.aphrodite.fe.base.BaseEvent;

public class TransactionDataFetchComplete extends BaseEvent {

    private final Boolean success;
    private final String message;
    private final Transaction header;
    private final List<TransactionItem> detail;

    public TransactionDataFetchComplete(Transaction header, List<TransactionItem> detail) {
        this.success = true;
        this.message = null;
        this.header = header;
        this.detail = detail;
    }
    public TransactionDataFetchComplete(String message) {
        this.success = false;
        this.message = message;
        header = null;
        detail = null;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Transaction getHeader() {
        return header;
    }

    public List<TransactionItem> getDetail() {
        return detail;
    }
}
