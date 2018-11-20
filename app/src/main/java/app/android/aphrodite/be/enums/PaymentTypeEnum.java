package app.android.aphrodite.be.enums;

import java.util.ArrayList;

public class PaymentTypeEnum {
    public static final String CASH = "Cash";
    public static final String BCA = "BCA";
    public static final String BRI = "BRI";

    public static ArrayList<String> list = new ArrayList<String>() {{
        add(CASH);
        add(BCA);
        add(BRI);
    }};
}
