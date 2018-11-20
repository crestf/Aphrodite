package app.android.aphrodite.fe.menu.inventory;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;
import java.util.Date;

import app.android.aphrodite.R;
import app.android.aphrodite.fe.common.HelperUtil;
import app.android.aphrodite.fe.common.SharedPrefManager;
import app.android.aphrodite.fe.menu.inventory.event.InventoryListFetchComplete;
import app.android.aphrodite.fe.base.BaseActivity;
import app.android.aphrodite.fe.menu.inventory.data.InventoryController;
import app.android.aphrodite.fe.menu.inventory.data.InventoryListAdapter;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemClick;

@SuppressWarnings({"ConstantConditions", "unused"})
public class InventoryActivity extends BaseActivity {

    @BindView(R.id.nav_view)        NavigationView navigationView;
    @BindView(R.id.drawer_layout)   DrawerLayout drawer;
    @BindView(R.id.fab)             FloatingActionButton fab;
//    @BindView(R.id.txtStartDate)    EditText txtStartDate;
//    @BindView(R.id.txtEndDate)      EditText txtEndDate;
    @BindView(R.id.lblNoData)       TextView lblNoData;
    @BindView(R.id.lvList)          ListView lvList;
    @BindView(R.id.cbActive)        CheckBox cbActive;
    @BindView(R.id.txtSearch)       EditText txtSearch;

    InventoryController controller = new InventoryController(this);
    InventoryListAdapter adapter;

    @SuppressWarnings("FieldCanBeLocal")
    private final long DELAY = 700; // milliseconds

    private final TextWatcher searchListener = new TextWatcher() {
        final Handler handler = new Handler();
        Runnable runnable;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            handler.removeCallbacks(runnable);
        }

        @Override
        public void afterTextChanged(Editable s) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    refreshContent();
                }
            };
            handler.postDelayed(runnable, DELAY);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(R.layout.inventory_activity);

//        txtStartDate.setText(SharedPrefManager.getInstance(this).getStartDate());
//        txtEndDate.setText(SharedPrefManager.getInstance(this).getEndDate());
        cbActive.setChecked(SharedPrefManager.getInstance(this).getShowInactive());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(InventoryActivity.this, AddEditInventoryActivity.class);
                startActivity(i);
                SharedPrefManager.getInstance(InventoryActivity.this).setIsUnlocked(true);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setTitle("Inventory");

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        adapter = new InventoryListAdapter(this, false);
        lvList.setAdapter(adapter);

        txtSearch.addTextChangedListener(searchListener);

//        lvList.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if((firstVisibleItem + visibleItemCount) >= adapter.getCount() - 7){
//                    Log.v("A", "Reached bottom");
//                }
//            }
//        });

        refreshContent();
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
        } else if (id == R.id.nav_transaction) {
            navigateTransaction();
        } else if (id == R.id.nav_recapitulation) {
            navigateRecapitulation();
        } else if (id == R.id.nav_settings) {
            navigateSettings();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnItemClick(R.id.lvList)
    public void lvListOnItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(this, InventoryDetailActivity.class);
        Bundle b = new Bundle();
        b.putInt("itemId", adapter.getItem(position).getId());
        i.putExtras(b);
        startActivity(i);
    }

//    @OnClick(R.id.txtStartDate)
//    public void txtStartDateOnClick() {
//        Calendar c = Calendar.getInstance();
//        c.setTime(HelperUtil.formatDisplayToDate(txtStartDate.getText().toString()));
//        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                Calendar c = Calendar.getInstance();
//                c.set(year, month, dayOfMonth);
//                Date d = c.getTime();
//                String date = HelperUtil.formatDateToDisplay(d);
//                txtStartDate.setText(date);
//                SharedPrefManager.getInstance(InventoryActivity.this).setStartDate(date);
//                refreshContent();
//            }
//        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
//        dialog.getDatePicker().setMaxDate(HelperUtil.formatDisplayToDate(txtEndDate.getText().toString()).getTime());
//        dialog.show();
//    }
//
//    @OnClick(R.id.txtEndDate)
//    public void txtEndDateOnClick() {
//        Calendar c = Calendar.getInstance();
//        c.setTime(HelperUtil.formatDisplayToDate(txtEndDate.getText().toString()));
//        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                Calendar c = Calendar.getInstance();
//                c.set(year, month, dayOfMonth);
//                Date d = c.getTime();
//                String date = HelperUtil.formatDateToDisplay(d);
//                txtEndDate.setText(date);
//                SharedPrefManager.getInstance(InventoryActivity.this).setEndDate(date);
//                refreshContent();
//            }
//        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
//        dialog.getDatePicker().setMinDate(HelperUtil.formatDisplayToDate(txtStartDate.getText().toString()).getTime());
//        dialog.show();
//    }

    @OnClick({R.id.lblCbActive, R.id.cbActive})
    public void lblCbActiveOnClick() {
        toggleActive();
    }

    private void toggleActive() {
        Boolean newVal = SharedPrefManager.getInstance(this).toggleShowInactive();
        cbActive.setChecked(newVal);
        refreshContent();
    }

    public void refreshContent() {
        setLoading(true);
        controller.fetchInventoryList(txtSearch.getText().toString());
    }

    @Subscribe
    public void refreshContentComplete(InventoryListFetchComplete event) {
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
            txtSearch.setEnabled(false);
        } else {
            loading.setVisibility(View.GONE);
            txtSearch.setEnabled(true);
        }
    }
}
