package app.android.aphrodite.fe.menu.inventory;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;

import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

import app.android.aphrodite.R;
import app.android.aphrodite.fe.common.HelperUtil;
import app.android.aphrodite.fe.common.SharedPrefManager;
import app.android.aphrodite.fe.menu.inventory.event.InventoryDataFetchComplete;
import app.android.aphrodite.fe.base.BaseActivity;
import app.android.aphrodite.fe.menu.inventory.data.InventoryController;
import app.android.aphrodite.fe.menu.inventory.event.InventorySaveComplete;
import app.android.aphrodite.be.model.Inventory;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressWarnings("ALL")
public class AddEditInventoryActivity extends BaseActivity {

    @BindView(R.id.toolbar)         Toolbar toolbar;
    @BindView(R.id.txtName)         EditText txtName;
    @BindView(R.id.txtHargaBeli)         EditText txtHargaBeli;
    @BindView(R.id.txtHargaJual)         EditText txtHargaJual;
    @BindView(R.id.txtQty)         EditText txtQty;
    @BindView(R.id.txtTransactionDate)  EditText txtTransactionDate;
    @BindView(R.id.tilName)  TextInputLayout tilName;
    @BindView(R.id.tilTransactionDate)  TextInputLayout tilTransactionDate;
    @BindView(R.id.tilHargaBeli)  TextInputLayout tilHargaBeli;
    @BindView(R.id.tilHargaJual)  TextInputLayout tilHargaJual;
    @BindView(R.id.tilQty)  TextInputLayout tilQty;
    @BindView(R.id.cbActive)            Switch cbActive;
    @BindView(R.id.loading)             RelativeLayout loading;

    private InventoryController controller = new InventoryController(this);

    private Boolean _editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addedit_inventory_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Date d = new Date();
        String date = HelperUtil.formatDateToDisplay(d);
        txtTransactionDate.setText(date);
        txtTransactionDate.setTag(d);

        prepareEditText();
        setView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void prepareEditText() {
        HelperUtil.setupEditText(txtHargaBeli);
        HelperUtil.setupEditText(txtHargaJual);
        HelperUtil.setupEditText(txtQty);
    }

    public void setView() {
        // Check if it's editing existing record
        Integer id = getIntent().getIntExtra("id", -1);
        if (id >= 0) {
            _editMode = true;
            getSupportActionBar().setTitle("Edit Item");
            txtName.setEnabled(false);
            cbActive.setVisibility(View.VISIBLE);
            setLoading(true);
            controller.fetchInventoryTransactionData(id);
            txtHargaBeli.setEnabled(false);
            txtHargaJual.setEnabled(false);
            return;
        }

        // Check if it's adding specific item
        Integer itemId = getIntent().getIntExtra("itemId", -1);
        if (itemId >= 0) {
            getSupportActionBar().setTitle("Add New Item");
            txtName.setEnabled(false);
            cbActive.setVisibility(View.GONE);
            setLoading(true);
            controller.fetchInventoryData(itemId);
            txtHargaBeli.setEnabled(false);
            txtHargaJual.setEnabled(false);
            return;
        }

        getSupportActionBar().setTitle("Add New Item");
        txtName.setEnabled(true);
        cbActive.setVisibility(View.GONE);
    }

    @Subscribe
    public void setDataComplete(InventoryDataFetchComplete event) {
        setLoading(false);
        if (!event.getSuccess()) {
            showError(event.getMessage());
        } else {
            txtName.setText(event.getData().getName());
            txtHargaBeli.setText(HelperUtil.formatCurrency(event.getData().getCapitalPrice()));
            txtHargaJual.setText(HelperUtil.formatCurrency(event.getData().getSellPrice()));
            txtTransactionDate.setText(HelperUtil.formatDBToDisplay(event.getData().getDate()));
            txtTransactionDate.setTag(HelperUtil.formatDBToDate(event.getData().getDate()));
            txtQty.setText(HelperUtil.formatCurrency(event.getData().getQuantity()));
            cbActive.setChecked(event.getData().getActive());
        }
    }

    public void setLoading(Boolean isLoading) {
        if (isLoading) {
            loading.setVisibility(View.VISIBLE);
        } else {
            loading.setVisibility(View.GONE);
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

    @OnClick(R.id.txtTransactionDate)
    public void txtTransactionDateOnClick() {
        Calendar c = Calendar.getInstance();
        c.setTime((Date)txtTransactionDate.getTag());
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth);
                Date d = c.getTime();
                String date = HelperUtil.formatDateToDisplay(d);
                txtTransactionDate.setText(date);
                txtTransactionDate.setTag(d);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    public void confirmBack() {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        AddEditInventoryActivity.super.realOnBackPressed();
                        SharedPrefManager.getInstance(AddEditInventoryActivity.this).setIsUnlocked(true);
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
        if (validate()) {
            setLoading(true);
            Inventory data = new Inventory(
                    HelperUtil.formatDateToDB((Date)txtTransactionDate.getTag()), txtName.getText().toString(),
                    HelperUtil.extractCurrency(txtHargaBeli),
                    HelperUtil.extractCurrency(txtHargaJual),
                    HelperUtil.extractCurrency(txtQty),
                    true);

            if (_editMode) {
                data.setId(getIntent().getIntExtra("id", -1));
                data.setActive(cbActive.isChecked());
            }

            controller.saveInventory(data);
        }
    }

    @Subscribe
    public void saveComplete(InventorySaveComplete event) {
        setLoading(false);
        if (event.getSuccess()) {
            showMessage(event.getMessage(), "Back", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            txtName.setText("");
            Date d = new Date();
            String date = HelperUtil.formatDateToDisplay(d);
            txtTransactionDate.setText(date);
            txtTransactionDate.setTag(d);
            txtHargaBeli.setText("");
            txtHargaJual.setText("");
            txtQty.setText("");

            if (_editMode) {
                finish();
            }
        } else {
            showError(event.getMessage());
        }
    }

    public Boolean validate() {
        Boolean result = true;
        tilName.setError(null);
        tilTransactionDate.setError(null);
        tilHargaBeli.setError(null);
        tilHargaJual.setError(null);
        tilQty.setError(null);

        if (txtName.getText().toString().isEmpty()) {
            showError("Item Name must be filled");
            tilName.setError("Item Name must be filled");
            result = false;
        }
        if (txtTransactionDate.getTag() == null) {
            showError("Transaction Date must be filled");
            tilTransactionDate.setError("Transaction Date must be filled");
            result = false;
        }
        if (txtHargaBeli.getText().toString().isEmpty()) {
            showError("Capital Price must be filled");
            tilHargaBeli.setError("Capital Price must be filled");
            result = false;
        }
        if (txtHargaJual.getText().toString().isEmpty()) {
            showError("Selling Price must be filled");
            tilHargaJual.setError("Selling Price must be filled");
            result = false;
        }
        if (txtQty.getText().toString().isEmpty()) {
            showError("Quantity must be filled");
            tilQty.setError("Quantity must be filled");
            result = false;
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        confirmBack();
    }

}
