package app.android.aphrodite.fe.menu.recapitulation;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;
import java.util.Date;

import app.android.aphrodite.R;
import app.android.aphrodite.be.model.Recap;
import app.android.aphrodite.fe.common.HelperUtil;
import app.android.aphrodite.fe.common.SharedPrefManager;
import app.android.aphrodite.fe.base.BaseActivity;
import app.android.aphrodite.fe.menu.recapitulation.data.RecapitulationController;
import app.android.aphrodite.fe.menu.recapitulation.data.RecapitulationListAdapter;
import app.android.aphrodite.fe.menu.transaction.TransactionActivity;
import app.android.aphrodite.fe.menu.transaction.event.RecapitulationListFetchComplete;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecapitulationActivity extends BaseActivity {

    @BindView(R.id.toolbar)         Toolbar toolbar;
    @BindView(R.id.nav_view)        NavigationView navigationView;
    @BindView(R.id.drawer_layout)   DrawerLayout drawer;
    @BindView(R.id.lblNoData)       TextView lblNoData;
    @BindView(R.id.lvList)          ListView lvList;
//    @BindView(R.id.cbActive)               CheckBox cbActive;
    @BindView(R.id.txtStartDate)
    EditText txtStartDate;
    @BindView(R.id.txtEndDate)      EditText txtEndDate;
    @BindView(R.id.lblHargaBeliTotal)        TextView lblHargaBeliTotal;
    @BindView(R.id.lblHargaJualTotal)        TextView lblHargaJualTotal;
    @BindView(R.id.lblTotalProfit)        TextView lblTotalProfit;

    RecapitulationController controller = new RecapitulationController(this);
    RecapitulationListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recapitulation_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        txtStartDate.setText(SharedPrefManager.getInstance(this).getStartDate());
        txtEndDate.setText(SharedPrefManager.getInstance(this).getEndDate());
//        cbActive.setChecked(SharedPrefManager.getInstance(this).getShowInactive());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setTitle("Recapitulation");

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(2).setChecked(true);

        adapter = new RecapitulationListAdapter(this);
        lvList.setAdapter(adapter);

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
                SharedPrefManager.getInstance(RecapitulationActivity.this).setStartDate(date);
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
                SharedPrefManager.getInstance(RecapitulationActivity.this).setEndDate(date);
                refreshContent();
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(HelperUtil.formatDisplayToDate(txtStartDate.getText().toString()).getTime());
        dialog.show();
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
            navigateTransaction();
        } else if (id == R.id.nav_recapitulation) {
        } else if (id == R.id.nav_settings) {
            navigateSettings();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    @OnClick({R.id.lblCbActive, R.id.cbActive})
//    public void lblCbActiveOnClick() {
//        toggleActive();
//    }
//
//    private void toggleActive() {
//        Boolean newVal = SharedPrefManager.getInstance(this).toggleShowInactive();
//        cbActive.setChecked(newVal);
//        refreshContent();
//    }

    public void refreshContent() {
        setLoading(true);
        controller.fetchRecapitulationList();
    }

    @Subscribe
    public void refreshContentComplete(RecapitulationListFetchComplete event) {
        setLoading(false);
        if (!event.getSuccess()) {
            showError(event.getMessage());
        } else {
            adapter.clear();
            adapter.addAll(event.getData());

            Double totalModal = 0d;
            Double totalIncome = 0d;
            for (Recap item : event.getData()) {
                totalModal += getVal(item.getModalBCA()) + getVal(item.getModalBRI()) + getVal(item.getModalCash());
                totalIncome += getVal(item.getTotalBCA()) + getVal(item.getTotalBRI()) + getVal(item.getTotalCash());
            }

            lblHargaBeliTotal.setText(HelperUtil.formatNumber(totalModal));
            lblHargaJualTotal.setText(HelperUtil.formatNumber(totalIncome));
            lblTotalProfit.setText(HelperUtil.formatNumber(totalIncome - totalModal));

            if (adapter.getCount() > 0) {
                lblNoData.setVisibility(View.GONE);
                lvList.setVisibility(View.VISIBLE);
            } else {
                lblNoData.setVisibility(View.VISIBLE);
                lvList.setVisibility(View.GONE);
            }
        }
    }

    public Double getVal(Double val) {
        if (val == null)
            return 0d;
        return val;
    }

    public void setLoading(Boolean isLoading) {
        if (isLoading) {
            loading.setVisibility(View.VISIBLE);
        } else {
            loading.setVisibility(View.GONE);
        }
    }
}
