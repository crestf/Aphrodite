package app.android.aphrodite.fe.base;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import app.android.aphrodite.R;
import app.android.aphrodite.fe.common.HelperUtil;

public abstract class BaseFragment extends Fragment {

    public void showMessage(String message) {
        HelperUtil.hideKeyboard(getActivity());
        showSnackbar(message, Color.GREEN, Color.WHITE, null, null);
    }
    public void showError(String message) {
        HelperUtil.hideKeyboard(getActivity());
        showSnackbar(message, Color.RED, Color.WHITE, null, null);
    }
    public void showMessage(String message, String actionString, View.OnClickListener callback) {
        HelperUtil.hideKeyboard(getActivity());
        showSnackbar(message, Color.GREEN, Color.WHITE, actionString, callback);
    }
    public void showError(String message, String actionString, View.OnClickListener callback) {
        HelperUtil.hideKeyboard(getActivity());
        showSnackbar(message, Color.RED, Color.WHITE, actionString, callback);
    }
    private void showSnackbar(String message, int textColor, int backgroundColor,
                              String actionString, final View.OnClickListener callback) {
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.coord), message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(backgroundColor);
        TextView text = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        text.setTextColor(textColor);
        if (actionString != null) {
            snackbar.setAction(actionString, callback);
        }
        snackbar.show();
    }
}
