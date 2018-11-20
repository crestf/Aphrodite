package app.android.aphrodite.fe.common;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HelperUtil {

    public static String formatDateToDisplay(Date d) {
        return DateFormat.format("dd MMM yyyy", d).toString();
    }
    public static String formatDateToDB(Date d) {
        return DateFormat.format("yyyy-MM-dd", d).toString();
    }


    public static String formatDateToFormat(Date d, String format) {
        return DateFormat.format(format, d).toString();
    }

    public static Date formatDBToDate(String s) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.parse(s);
        } catch (Exception e) {
            return null;
        }
    }
    public static Date formatDisplayToDate(String s) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
            return format.parse(s);
        } catch (Exception e) {
            return null;
        }
    }
    public static String formatDisplayToDB(String s) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
            Date date = format.parse(s);
            return formatDateToDB(date);
        } catch (Exception e) {
            return null;
        }
    }
    public static String formatDBToDisplay(String s) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(s);
            return formatDateToDisplay(date);
        } catch (Exception e) {
            return null;
        }
    }

    public static String formatNumber(Double d) {
        if (d == null)
            return "0";

        NumberFormat formatter = new DecimalFormat("#,###");
        return formatter.format(d);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String formatCurrency(Double d) {
        NumberFormat formatter = new DecimalFormat("#,###");
        return formatter.format(d);
    }

    public static void setupEditText(EditText view) {
        View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText txt = (EditText) v;
                if (hasFocus) {
                    String s = txt.getText().toString().replace(",", "");
                    txt.setText(s);
                } else {
                    Double d = txt.getText().toString().isEmpty() ? 0d : Double.valueOf(txt.getText().toString());
                    String s = formatCurrency(d);
                    txt.setText(s);
                }
            }
        };
        view.setOnFocusChangeListener(listener);
    }

    public static Double extractCurrency(EditText view) {
        String s = view.getText().toString().replace(",", "");
        if (s.isEmpty()) {
            return 0d;
        }
        return Double.valueOf(s);
    }
}
