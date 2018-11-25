package app.android.aphrodite.fe.menu.transaction;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import app.android.aphrodite.R;
import app.android.aphrodite.be.model.Transaction;
import app.android.aphrodite.be.model.TransactionItem;
import app.android.aphrodite.fe.common.GlobalState;
import app.android.aphrodite.fe.common.HelperUtil;
import app.android.aphrodite.fe.common.SharedPrefManager;
import app.android.aphrodite.fe.base.BaseActivity;
import app.android.aphrodite.be.enums.TransactionStatusEnum;
import app.android.aphrodite.fe.menu.transaction.data.TransactionController;
import app.android.aphrodite.fe.menu.transaction.data.TransactionListAdapter;
import app.android.aphrodite.fe.menu.transaction.event.TransactionDataFetchComplete;
import app.android.aphrodite.fe.menu.transaction.event.TransactionListFetchComplete;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class TransactionActivity extends BaseActivity {

    @BindView(R.id.toolbar)         Toolbar toolbar;
    @BindView(R.id.nav_view)        NavigationView navigationView;
    @BindView(R.id.drawer_layout)   DrawerLayout drawer;
    @BindView(R.id.fab)             FloatingActionButton fab;
    @BindView(R.id.txtStartDate)    EditText txtStartDate;
    @BindView(R.id.txtEndDate)      EditText txtEndDate;
    @BindView(R.id.txtStatus)       EditText txtStatus;
    @BindView(R.id.lblNoData)       TextView lblNoData;
    @BindView(R.id.lvList)          ListView lvList;
    @BindView(R.id.cbActive)
    CheckBox cbActive;

    TransactionController controller = new TransactionController(this);
    TransactionListAdapter adapter;
    PopupWindow popupWindow;
    ArrayList<String> statusList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        txtStartDate.setText(SharedPrefManager.getInstance(this).getStartDate());
        txtEndDate.setText(SharedPrefManager.getInstance(this).getEndDate());
        txtStatus.setText(SharedPrefManager.getInstance(this).getStatus());
        cbActive.setChecked(SharedPrefManager.getInstance(this).getShowInactive());

        statusList.add(TransactionStatusEnum.ALL);
        statusList.add(TransactionStatusEnum.NONE);
        statusList.add(TransactionStatusEnum.WAITING);
        statusList.add(TransactionStatusEnum.READY);
        statusList.add(TransactionStatusEnum.FINISHED);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddEditPage(null, null);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setTitle("Transaction");

        adapter = new TransactionListAdapter(this);
        lvList.setAdapter(adapter);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(1).setChecked(true);

        refreshContent();
    }

    private void openAddEditPage(Transaction header, List<TransactionItem> detail) {
        GlobalState.getInstance().clearTransactionData();
        if (header != null && detail != null) {
            GlobalState.getInstance().putTransactionData(header, detail);
        }
        Intent i = new Intent(TransactionActivity.this, AddEditTransactionActivity.class);
        startActivity(i);
    }

    @OnItemClick(R.id.lvList)
    public void lvListOnItemClick(AdapterView<?> parent, View view, int position, long id) {
        setLoading(true);
        controller.fetchTransactionData(adapter.getItem(position).getId());
    }

    @Subscribe
    public void onDataFetched(TransactionDataFetchComplete event) {
        setLoading(false);
        if (!event.getSuccess()) {
            showError(event.getMessage());
        } else {
            openAddEditPage(event.getHeader(), event.getDetail());
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_master_inventory) {
            navigateMasterInventory();
        } else if (id == R.id.nav_transaction) {
        } else if (id == R.id.nav_recapitulation) {
            navigateRecapitulation();
        } else if (id == R.id.nav_settings) {
            navigateSettings();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick({R.id.lblCbActive, R.id.cbActive})
    public void lblCbActiveOnClick() {
        toggleActive();
    }

    private void toggleActive() {
        Boolean newVal = SharedPrefManager.getInstance(this).toggleShowInactive();
        cbActive.setChecked(newVal);
        refreshContent();
    }

    @OnClick(R.id.txtStartDate)
    public void txtStartDateOnClick() {
        Calendar c = Calendar.getInstance();
        c.setTime(HelperUtil.formatDisplayToDate(txtStartDate.getText().toString()));
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth);
                Date d = c.getTime();
                String date = HelperUtil.formatDateToDisplay(d);
                txtStartDate.setText(date);
                SharedPrefManager.getInstance(TransactionActivity.this).setStartDate(date);
                refreshContent();
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMaxDate(HelperUtil.formatDisplayToDate(txtEndDate.getText().toString()).getTime());
        dialog.show();
    }

    @OnClick(R.id.txtEndDate)
    public void txtEndDateOnClick() {
        Calendar c = Calendar.getInstance();
        c.setTime(HelperUtil.formatDisplayToDate(txtEndDate.getText().toString()));
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth);
                Date d = c.getTime();
                String date = HelperUtil.formatDateToDisplay(d);
                txtEndDate.setText(date);
                SharedPrefManager.getInstance(TransactionActivity.this).setEndDate(date);
                refreshContent();
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(HelperUtil.formatDisplayToDate(txtStartDate.getText().toString()).getTime());
        dialog.show();
    }

    @OnClick(R.id.txtStatus)
    public void txtStatusOnClick() {
        PopupWindow p = popupWindowsort();
        p.showAsDropDown(txtStatus, 0, 0);
    }

    public void refreshContent() {
        setLoading(true);
        controller.fetchTransactionList(txtStatus.getText().toString());
    }

    @Subscribe
    public void refreshContentComplete(TransactionListFetchComplete event) {
        setLoading(false);
        if (!event.getSuccess()) {
            showError(event.getMessage());
        } else {
            adapter.clear();
            adapter.addAll(event.getData());

            if (adapter.getCount() > 0) {
                lblNoData.setVisibility(View.GONE);
                lvList.setVisibility(View.VISIBLE);
            } else {
                lblNoData.setVisibility(View.VISIBLE);
                lvList.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshContent();
    }

    public void setLoading(Boolean isLoading) {
        if (isLoading) {
            loading.setVisibility(View.VISIBLE);
        } else {
            loading.setVisibility(View.GONE);
        }
    }

    private PopupWindow popupWindowsort() {
        // initialize a pop up window type
        popupWindow = new PopupWindow(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                statusList);
        // the drop down list is a list view
        ListView listViewSort = new ListView(this);

        // set our adapter and pass our pop up window contents
        listViewSort.setAdapter(adapter);

        // set on item selected
        listViewSort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                txtStatus.setText(statusList.get(position));
                SharedPrefManager.getInstance(TransactionActivity.this).setStatus(statusList.get(position));
                refreshContent();
                popupWindow.dismiss();
            }
        });

        // some other visual settings for popup window
        popupWindow.setFocusable(true);
        popupWindow.setWidth(250);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.dark_gray));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // set the listview as popup content
        popupWindow.setContentView(listViewSort);

        return popupWindow;
    }
}
