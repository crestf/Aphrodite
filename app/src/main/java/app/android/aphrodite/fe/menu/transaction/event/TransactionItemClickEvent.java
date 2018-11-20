package app.android.aphrodite.fe.menu.transaction.event;

import app.android.aphrodite.fe.base.BaseEvent;

public class TransactionItemClickEvent extends BaseEvent {

    private final Integer index;

    public TransactionItemClickEvent(Integer index) {
        this.index = index;
    }

    public Integer getIndex() {
        return index;
    }
}
