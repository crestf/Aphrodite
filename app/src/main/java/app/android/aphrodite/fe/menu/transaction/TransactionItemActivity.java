package app.android.aphrodite.fe.menu.transaction;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;

import app.android.aphrodite.R;
import app.android.aphrodite.fe.common.HelperUtil;
import app.android.aphrodite.fe.menu.transaction.event.TransactionItemChangeEvent;
import app.android.aphrodite.fe.base.BaseActivity;
import app.android.aphrodite.be.model.TransactionItem;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TransactionItemActivity extends BaseActivity {

    @BindView(R.id.toolbar)         Toolbar toolbar;
    @BindView(R.id.txtName)         EditText txtName;
    @BindView(R.id.txtHargaBeli)    EditText txtHargaBeli;
    @BindView(R.id.txtHargaJual)    EditText txtHargaJual;
    @BindView(R.id.txtQty)          EditText txtQty;

    private Integer originalIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addedit_transaction_item_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        originalIndex = getIntent().getIntExtra("index", -1);

        HelperUtil.setupEditText(txtHargaJual);
        HelperUtil.setupEditText(txtHargaBeli);
        HelperUtil.setupEditText(txtQty);


        setView();
        getSupportActionBar().setTitle("Add Transaction Item");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setView() {
        String name = getIntent().getStringExtra("name");
        if (name != null) {
            getSupportActionBar().setTitle("Edit Transaction Item");

            Double capitalPrice = getIntent().getDoubleExtra("capitalPrice", 0d);
            Double sellPrice = getIntent().getDoubleExtra("sellPrice", 0d);
            Double qty = getIntent().getDoubleExtra("qty", 0d);


            txtName.setText(name);
            txtHargaJual.setText(HelperUtil.formatCurrency(sellPrice));
            txtHargaBeli.setText(HelperUtil.formatCurrency(capitalPrice));
            txtQty.setText(HelperUtil.formatCurrency(qty));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                confirmBack();
                return true;
            case R.id.action_save:
                save();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        confirmBack();
    }

    public void confirmBack() {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        TransactionItemActivity.super.realOnBackPressed();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Any unsaved data will be lost.")
                .setTitle("Are you sure to back?")
                .setPositiveButton("Back", listener)
                .setNegativeButton("Cancel", listener)
                .show();
    }

    public void save() {
        if (txtName.getText().toString().equalsIgnoreCase("") ||
                txtHargaBeli.getText().toString().equalsIgnoreCase("") ||
                txtHargaJual.getText().toString().equalsIgnoreCase("") ||
                txtQty.getText().toString().equalsIgnoreCase("")) {
            showError("Please enter all fields");
            return;
        }

        TransactionItem data = new TransactionItem(
                null,
                txtName.getText().toString(),
                HelperUtil.extractCurrency(txtHargaBeli),
                HelperUtil.extractCurrency(txtHargaJual),
                HelperUtil.extractCurrency(txtQty)
        );
        EventBus.getDefault().post(new TransactionItemChangeEvent(originalIndex, data));
        finish();
    }

}
