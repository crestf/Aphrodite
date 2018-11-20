package app.android.aphrodite.fe.common;

import java.util.List;

import app.android.aphrodite.be.model.Transaction;
import app.android.aphrodite.be.model.TransactionItem;

public class GlobalState {
    private static final GlobalState ourInstance = new GlobalState();

    public static GlobalState getInstance() {
        return ourInstance;
    }

    private GlobalState() {
    }

    private Transaction header;
    private List<TransactionItem> detail;

    public void putTransactionData(Transaction header, List<TransactionItem> detail) {
        this.header = header;
        this.detail = detail;
    }

    public Transaction getTransactionData() {
        if (header == null)
            return null;

        header.setItems(detail);
        return header;
    }

    public void clearTransactionData() {
        this.header = null;
        this.detail = null;
    }
}
