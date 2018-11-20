package app.android.aphrodite;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.os.Bundle;

import app.android.aphrodite.fe.common.SharedPrefManager;


public class MyApplication extends Application {

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            SharedPrefManager.getInstance(this).setIsUnlocked(false);
        }
    }


}
