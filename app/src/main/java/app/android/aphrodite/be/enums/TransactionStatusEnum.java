package app.android.aphrodite.be.enums;

import java.util.ArrayList;

public class TransactionStatusEnum {
    public static final String ALL = "All";
    public static final String NONE = "None";
    public static final String WAITING = "Waiting";
    public static final String READY = "Ready";
    public static final String FINISHED = "Finished";

    public static ArrayList<String> list = new ArrayList<String>() {{
        add(NONE);
        add(WAITING);
        add(READY);
        add(FINISHED);
    }};
}
