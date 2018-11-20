package app.android.aphrodite.be.enums;

import java.util.ArrayList;

public class TransactionTypeEnum {
    public static final String READY_STOCK = "Ready Stock";
    public static final String PRE_ORDER = "PO";

    public static ArrayList<String> list = new ArrayList<String>() {{
        add(READY_STOCK);
        add(PRE_ORDER);
    }};
}
