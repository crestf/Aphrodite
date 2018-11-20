package app.android.aphrodite.fe.menu.transaction;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import app.android.aphrodite.R;
import app.android.aphrodite.fe.common.GlobalState;
import app.android.aphrodite.fe.common.HelperUtil;
import app.android.aphrodite.fe.menu.inventory.AddEditInventoryActivity;
import app.android.aphrodite.fe.menu.transaction.event.TransactionDataFetchComplete;
import app.android.aphrodite.fe.menu.transaction.event.TransactionItemChangeEvent;
import app.android.aphrodite.fe.base.BaseActivity;
import app.android.aphrodite.fe.menu.transaction.data.TransactionController;
import app.android.aphrodite.fe.menu.transaction.data.ViewPagerAdapter;
import app.android.aphrodite.be.model.Inventory;
import app.android.aphrodite.be.model.Transaction;
import app.android.aphrodite.be.model.TransactionItem;
import app.android.aphrodite.fe.menu.transaction.event.TransactionSaveComplete;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddEditTransactionActivity extends BaseActivity {

    @BindView(R.id.toolbar)         Toolbar toolbar;
    @BindView(R.id.tabs)            TabLayout tabs;
    @BindView(R.id.viewPager)       ViewPager viewPager;
    @BindView(R.id.fab)             FloatingActionButton fab;

    TransactionController controller = new TransactionController(this);
    private ViewPagerAdapter adapter;
    private boolean isEditMode = false;

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(final String newTransactionType) {
        final String prevType = this.transactionType;
        if (prevType == null || !prevType.equalsIgnoreCase(newTransactionType)) {
            if (adapter.getDetail().getDetailData() != null && adapter.getDetail().getDetailData().size() > 0) {
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                AddEditTransactionActivity.this.transactionType = newTransactionType;
                                adapter.getDetail().setDetailData(new ArrayList<TransactionItem>());
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                AddEditTransactionActivity.this.transactionType = prevType;
                                adapter.getHeader().txtTransactionType.setText(prevType);
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Changing Transaction Type will clear all inputted items")
                        .setTitle("Change Transaction Type?")
                        .setPositiveButton("Change", listener)
                        .setNegativeButton("Cancel", listener)
                        .show();
            } else {
                this.transactionType = newTransactionType;
            }
        }
    }

    private String transactionType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addedit_transaction_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        if (isEditMode) {
            getSupportActionBar().setTitle("Edit Item");
        } else {
            getSupportActionBar().setTitle("Add New Item");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onPageSelected(int i) {
                HelperUtil.hideKeyboard(AddEditTransactionActivity.this);

                switch (i) {
                    case 0:
                        fab.setVisibility(View.GONE);
                        break;
                    case 1:
                        fab.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        setView();
    }

    public void setView() {
        // Check if it's editing existing record
        Integer id = getIntent().getIntExtra("id", -1);
        if (id < 0) {
            return;
        }

        setTransactionType(GlobalState.getInstance().getTransactionData().getType());

//        setLoading(true);
//        controller.fetchTransactionData(id);
    }

//    @Subscribe
//    public void onDataFetched(TransactionDataFetchComplete event) {
//        setLoading(false);
//        if (!event.getSuccess()) {
//            showError(event.getMessage());
//        } else {
//            setTransactionType(event.getHeader().getType());
//            adapter.getHeader().setHeaderData(event.getHeader());
//            adapter.getDetail().setDetailData(event.getDetail());
//        }
//    }

    public void addItem() {
        if (transactionType.equalsIgnoreCase("PO")) {
            Intent i = new Intent(this, TransactionItemActivity.class);
            Bundle b = new Bundle();
            b.putInt("index", -1);
            i.putExtras(b);
            startActivity(i);
        } else {
            showAddDialog();
        }
    }

    private void showAddDialog() {
        DialogFragment newFragment = InventoryPickerDialogFragment.newInstance(new InventoryPickerDialogFragment.Callback() {
            @Override
            public void onItemSelected(Inventory item, Double qty) {
                TransactionItem data = new TransactionItem(null, item.getName(), item.getCapitalPrice(), item.getSellPrice(), qty);
                EventBus.getDefault().post(new TransactionItemChangeEvent(-1, data));
            }
        }, null);
        newFragment.show(getSupportFragmentManager(), "dialog");
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

    public void confirmBack() {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        AddEditTransactionActivity.super.realOnBackPressed();
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
        if (!adapter.getHeader().validate()) {
             viewPager.setCurrentItem(0);
             return;
        }
        if (!adapter.getDetail().validate()) {
            showError("No transaction item");
            viewPager.setCurrentItem(1);
            return;
        }

        setLoading(true);
        Transaction header = adapter.getHeader().getHeaderData();
        List<TransactionItem> details = adapter.getDetail().getDetailData();

        controller.saveTransaction(header, details);
    }

    @Subscribe
    public void onSaveComplete(TransactionSaveComplete event) {
        setLoading(false);
        if (!event.getSuccess()) {
            showError(event.getMessage());
        } else {
            showMessage("Data saved");
            finish();
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
    public void onBackPressed() {
        confirmBack();
    }

}
