package app.android.aphrodite.fe.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import app.android.aphrodite.R;
import app.android.aphrodite.fe.common.HelperUtil;
import app.android.aphrodite.fe.common.SharedPrefManager;
import app.android.aphrodite.fe.event.ForceLogoutEvent;
import app.android.aphrodite.fe.menu.activation.ActivationActivity;
import app.android.aphrodite.fe.menu.inventory.InventoryActivity;
import app.android.aphrodite.fe.menu.recapitulation.RecapitulationActivity;
import app.android.aphrodite.fe.menu.security.SecurityActivity;
import app.android.aphrodite.fe.menu.settings.SettingsActivity;
import app.android.aphrodite.fe.menu.transaction.TransactionActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)     public Toolbar toolbar;
    @BindView(R.id.loading)     public RelativeLayout loading;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public void checkSecurity() {
        boolean isActivated = SharedPrefManager.getInstance(getApplicationContext()).getIsActivated();
        if (!isActivated) {
            Intent i = new Intent(this, ActivationActivity.class);
            startActivity(i);
            finish();
            return;
        }

        boolean isUnlocked = SharedPrefManager.getInstance(getApplicationContext()).getIsUnlocked();
        if (!isUnlocked) {
            Intent i = new Intent(this, SecurityActivity.class);
            startActivity(i);
            return;
        }
    }

    public void initView(int view) {
        setContentView(view);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkSecurity();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            realOnBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Subscribe
    public void onForceLogout(ForceLogoutEvent event) {

    }

    protected void realOnBackPressed() {
        super.onBackPressed();
    }

    public void navigateMasterInventory() {
        Intent i = new Intent(this, InventoryActivity.class);
        startActivity(i);
        finish();
        SharedPrefManager.getInstance(this).setIsUnlocked(true);
    }
    public void navigateTransaction() {
        Intent i = new Intent(this, TransactionActivity.class);
        startActivity(i);
        finish();
        SharedPrefManager.getInstance(this).setIsUnlocked(true);
    }
    public void navigateRecapitulation() {
        Intent i = new Intent(this, RecapitulationActivity.class);
        startActivity(i);
        finish();
        SharedPrefManager.getInstance(this).setIsUnlocked(true);
    }
    public void navigateSettings() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
        finish();
        SharedPrefManager.getInstance(this).setIsUnlocked(true);
    }

    public void showMessage(String message) {
        HelperUtil.hideKeyboard(this);
        showSnackbar(message, Color.GREEN, Color.WHITE, null, null);
    }
    public void showError(String message) {
        HelperUtil.hideKeyboard(this);
        showSnackbar(message, Color.RED, Color.WHITE, null, null);
    }
    public void showMessage(String message, String actionString, View.OnClickListener callback) {
        HelperUtil.hideKeyboard(this);
        showSnackbar(message, Color.GREEN, Color.WHITE, actionString, callback);
    }
    public void showError(String message, String actionString, View.OnClickListener callback) {
        HelperUtil.hideKeyboard(this);
        showSnackbar(message, Color.RED, Color.WHITE, actionString, callback);
    }
    private void showSnackbar(String message, int textColor, int backgroundColor,
                              String actionString, final View.OnClickListener callback) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.coord), message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(backgroundColor);
        TextView text = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        text.setTextColor(textColor);
        if (actionString != null) {
            snackbar.setAction(actionString, callback);
        }
        snackbar.show();
    }
}
