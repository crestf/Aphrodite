package app.android.aphrodite.fe.menu.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import app.android.aphrodite.R;
import app.android.aphrodite.fe.base.BaseActivity;
import app.android.aphrodite.fe.common.SharedPrefManager;
import app.android.aphrodite.fe.menu.settings.event.ImportExportEvent;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity {

    @BindView(R.id.toolbar)         Toolbar toolbar;
    @BindView(R.id.nav_view)        NavigationView navigationView;
    @BindView(R.id.drawer_layout)   DrawerLayout drawer;

    @BindView(R.id.txtOldPIN)       EditText txtOldPin;
    @BindView(R.id.txtNewPIN)       EditText txtNewPIN;
    @BindView(R.id.txtConfirmNewPIN)    EditText txtConfirmNewPIN;
    @BindView(R.id.lblErrorPIN)         TextView lblErrorPIN;
    @BindView(R.id.lblSuccessPIN)       TextView lblSuccessPIN;
    @BindView(R.id.lblErrorImportExport)         TextView lblErrorImportExport;
    @BindView(R.id.lblSuccessImportExport)       TextView lblSuccessImportExport;


    SettingController controller = new SettingController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(3).getSubMenu().getItem(0).setChecked(true);
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
            navigateRecapitulation();
        } else if (id == R.id.nav_settings) {
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick(R.id.btnChangePIN)
    public void onPINChanged() {
        lblErrorPIN.setText("");
        lblSuccessPIN.setText("");

        if (!txtOldPin.getText().toString().equalsIgnoreCase(SharedPrefManager.getInstance(this).getUnlockCode())) {
            lblErrorPIN.setText("Old PIN doesn't match");
            return;
        }

        if (!txtNewPIN.getText().toString().equalsIgnoreCase(txtConfirmNewPIN.getText().toString())) {
            lblErrorPIN.setText("New PIN confirmation is failed");
            return;
        }

        SharedPrefManager.getInstance(this).setUnlockCode(txtNewPIN.getText().toString());
        txtNewPIN.setText("");
        txtOldPin.setText("");
        txtConfirmNewPIN.setText("");
        lblSuccessPIN.setText("PIN successfully changed");
    }

    public void setLoading(Boolean isLoading) {
        if (isLoading) {
            loading.setVisibility(View.VISIBLE);
        } else {
            loading.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btnImport)
    public void onImport() {
        Intent intent = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
        SharedPrefManager.getInstance(this).setIsUnlocked(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==123 && resultCode==RESULT_OK) {
            Uri selectedfile = data.getData();
            setLoading(true);
            controller.importData(selectedfile);
        }
    }

    @OnClick(R.id.btnExport)
    public void onExport() {
        setLoading(true);
        controller.exportData();
    }

    @Subscribe
    public void onImportExport(ImportExportEvent event) {
        setLoading(false);
        if (event.getSuccess()) {
            lblSuccessImportExport.setText(event.getMessage());
        } else {
            lblErrorImportExport.setText(event.getMessage());
        }
    }
}
