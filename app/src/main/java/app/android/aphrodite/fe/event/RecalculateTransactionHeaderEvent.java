package app.android.aphrodite.fe.event;

public class RecalculateTransactionHeaderEvent {

    private final Double total;

    public RecalculateTransactionHeaderEvent(Double total) {
        this.total = total;
    }

    public Double getTotal() {
        return total;
    }

}
