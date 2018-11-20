package app.android.aphrodite.fe.menu.security;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import app.android.aphrodite.R;
import app.android.aphrodite.be.enums.TransactionStatusEnum;
import app.android.aphrodite.fe.base.BaseActivity;
import app.android.aphrodite.fe.common.HelperUtil;
import app.android.aphrodite.fe.common.SharedPrefManager;
import app.android.aphrodite.fe.menu.inventory.InventoryActivity;
import app.android.aphrodite.fe.menu.transaction.AddEditTransactionActivity;
import app.android.aphrodite.fe.menu.transaction.data.TransactionController;
import app.android.aphrodite.fe.menu.transaction.data.TransactionListAdapter;
import app.android.aphrodite.fe.menu.transaction.event.TransactionListFetchComplete;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class SecurityActivity extends AppCompatActivity {

    private String _input = "";
    private Boolean successFlag = false;

    @BindView(R.id.txtInput) EditText txtInput;
    @BindView(R.id.lblError) TextView lblError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.security_activity);
        ButterKnife.bind(this);
        HelperUtil.hideKeyboard(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        HelperUtil.hideKeyboard(this);
    }

    @OnClick({ R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
                R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
                R.id.btn8, R.id.btn9})
    public void onBtnInputClick(View v) {
        if (_input.length() < 8) {
            _input = _input + ((Button) v).getText().toString();
            refreshView();
        }
    }

    @OnClick(R.id.btnDel)
    public void onBtnDelClick() {
        if (_input.length() > 0) {
            _input = _input.substring(0, _input.length() - 1);
            refreshView();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @OnClick(R.id.btnOk)
    public void onBtnOk() {
        if (_input.equalsIgnoreCase(SharedPrefManager.getInstance(this).getUnlockCode())) {
            SharedPrefManager.getInstance(this).setIsUnlocked(true);
            successFlag = true;

            finish();
        } else {
            lblError.setText("Wrong Security Code !");
        }
    }

    public void refreshView() {
        StringBuilder a = new StringBuilder();
        for (int i=0; i<_input.length(); i++) {
            a.append("*");
        }
        txtInput.setText(a.toString());
    }
}
