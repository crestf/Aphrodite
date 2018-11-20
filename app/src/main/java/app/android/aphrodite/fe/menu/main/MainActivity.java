package app.android.aphrodite.fe.menu.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import app.android.aphrodite.R;
import app.android.aphrodite.fe.common.SharedPrefManager;
import app.android.aphrodite.fe.menu.inventory.InventoryActivity;
import app.android.aphrodite.fe.menu.security.SecurityActivity;

public class MainActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Intent i = new Intent(this, InventoryActivity.class);
        startActivity(i);
        finish();
    }

    public void checkSecurity() {
        boolean isActivated = SharedPrefManager.getInstance(getApplicationContext()).getIsActivated();
        if (!isActivated) {

//            return;
        }

        boolean isUnlocked = SharedPrefManager.getInstance(getApplicationContext()).getIsUnlocked();
        if (!isUnlocked) {
            Intent i = new Intent(this, SecurityActivity.class);
            startActivity(i);
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkSecurity();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
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

}
