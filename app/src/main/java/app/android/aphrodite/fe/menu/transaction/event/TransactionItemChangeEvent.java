package app.android.aphrodite.fe.menu.transaction.event;

import app.android.aphrodite.be.model.TransactionItem;

public class TransactionItemChangeEvent {

    private final Integer originalIndex;
    private final TransactionItem data;

    public TransactionItemChangeEvent(Integer originalIndex, TransactionItem data) {
        this.originalIndex = originalIndex;
        this.data = data;
    }

    public Integer getOriginalIndex() {
        return originalIndex;
    }

    public TransactionItem getData() {
        return data;
    }
}
