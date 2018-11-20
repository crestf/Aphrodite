package app.android.aphrodite.fe.menu.transaction.event;

import app.android.aphrodite.fe.base.BaseEvent;

public class TransactionItemDeleteEvent extends BaseEvent {

    private final Integer index;

    public TransactionItemDeleteEvent(Integer index) {
        this.index = index;
    }

    public Integer getIndex() {
        return index;
    }
}
