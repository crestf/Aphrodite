package app.android.aphrodite.fe.menu.activation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

import app.android.aphrodite.R;
import app.android.aphrodite.fe.common.HelperUtil;
import app.android.aphrodite.fe.common.SharedPrefManager;
import app.android.aphrodite.fe.menu.inventory.InventoryActivity;
import app.android.aphrodite.fe.menu.security.SecurityActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivationActivity extends AppCompatActivity {

    private String _key = "";
    private String _input = "";

    @BindView(R.id.txtKey) TextView txtKey;
    @BindView(R.id.txtInput) EditText txtInput;
    @BindView(R.id.lblError) TextView lblError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activation_activity);
        ButterKnife.bind(this);
        HelperUtil.hideKeyboard(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        generateKey();
    }

    private void generateKey() {
        while (_key.length() != 6) {
            Random r = new Random();
            _key = String.valueOf(r.nextInt(888888) + 123456);
        }
        txtKey.setText(_key);
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
        if (_input.length() < 4) {
            _input = _input + ((Button) v).getText().toString();
            refreshView();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    @OnClick(R.id.btnDel)
    public void onBtnDelClick() {
        if (_input.length() > 0) {
            _input = _input.substring(0, _input.length() - 1);
            refreshView();
        }
    }

    @OnClick(R.id.btnOk)
    public void onBtnOk() {
        if (validate(_input, _key)) {
            SharedPrefManager.getInstance(this).setIsActivated(true);
            SharedPrefManager.getInstance(this).setIsUnlocked(true);

            Intent i = new Intent(this, InventoryActivity.class);
            startActivity(i);
            finish();
        } else {
            lblError.setText("Wrong Activation Code !");
        }
    }

    private boolean validate(String input, String key) {
        return getUnlockKey(key).equalsIgnoreCase(input);
    }

    private String getUnlockKey(String key) {
        long t = Integer.valueOf(DateFormat.format("yyyyMMdd", new Date()).toString());
        long front = Long.valueOf(key.substring(0, 4));
        long back = Long.valueOf(key.substring(4, 6));
        String combination = String.valueOf(t * back * 100 + front);

        Integer firstDigit = Integer.valueOf(combination.substring(0, 1));
        Integer lastDigit = Integer.valueOf(combination.substring(combination.length()-1, combination.length()));
        Integer fourthDigit = Integer.valueOf(combination.substring(4, 5));
        Integer nLstDigit = 0;
        try {
            nLstDigit = (Integer.valueOf(combination.substring(fourthDigit, fourthDigit + 1)));
        } catch (Exception e) { }

        String key1 = String.valueOf(Math.abs(fourthDigit - lastDigit));
        String key2 = String.valueOf((firstDigit * lastDigit) % 10);
        String key3 = String.valueOf(fourthDigit);
        String key4 = String.valueOf(nLstDigit);

        return key1 + key2 + key3 + key4;
    }

    public void refreshView() {
        StringBuilder a = new StringBuilder();
        for (int i=0; i<_input.length(); i++) {
            a.append(_input.substring(i, i+1));
        }
        txtInput.setText(a.toString());
    }
}
