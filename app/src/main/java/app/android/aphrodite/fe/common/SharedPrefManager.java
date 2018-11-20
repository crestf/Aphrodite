package app.android.aphrodite.fe.common;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class SharedPrefManager {

    private static SharedPrefManager instance;
    private SharedPreferences sharedpreferences;

    private final String pref_key = "aphroditePref";

    private final String IS_ACTIVATED = "is-activated";
    private final String IS_UNLOCKED = "is-unlocked";
    private final String ACTIVATION_CODE = "activation-code";
    private final String UNLOCK_CODE = "unlock-code";

    private final String START_DATE = "start-date";
    private final String END_DATE = "end-date";
    private final String STATUS = "status";
    private final String SHOW_INACTIVE = "show-inactive";

    public static SharedPrefManager getInstance(Context context) {
        if (instance == null)
            instance = new SharedPrefManager();

        instance.sharedpreferences = context.getSharedPreferences(instance.pref_key, Context.MODE_PRIVATE);

        return instance;
    }

    private void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    private boolean getBoolean(String key, boolean defValue) {
        return sharedpreferences.getBoolean(key, defValue);
    }
    private void setString(String key, String value) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    private String getString(String key, String defValue) {
        return sharedpreferences.getString(key, defValue);
    }



    public boolean getIsActivated() {
        return getBoolean(IS_ACTIVATED, false);
    }
    public void setIsActivated(boolean isActivated) {
        setBoolean(IS_ACTIVATED, isActivated);
    }

    public boolean getIsUnlocked() {
        return getBoolean(IS_UNLOCKED, false);
    }
    public void setIsUnlocked(boolean isUnlocked) {
        setBoolean(IS_UNLOCKED, isUnlocked);
    }

    public String getActivationCode() {
        return getString(ACTIVATION_CODE, null);
    }
    public void setActivationCode(String activationCode) {
        setString(ACTIVATION_CODE, activationCode);
    }

    public String getUnlockCode() { return getString(UNLOCK_CODE, "0000"); }
    public void setUnlockCode(String unlockCode) {
        setString(UNLOCK_CODE, unlockCode);
    }

    public String getStartDate() { return getString(START_DATE, HelperUtil.formatDateToDisplay(new Date())); }
    public void setStartDate(String startDate) {
        setString(START_DATE, startDate);
    }

    public String getEndDate() { return getString(END_DATE, HelperUtil.formatDateToDisplay(new Date())); }
    public void setEndDate(String endDate) {
        setString(END_DATE, endDate);
    }

    public String getStatus() { return getString(STATUS, "All"); }
    public void setStatus(String status) {
        setString(STATUS, status);
    }

    public boolean getShowInactive() {
        return getBoolean(SHOW_INACTIVE, false);
    }
    public void setShowInactive(boolean showInactive) {
        setBoolean(SHOW_INACTIVE, showInactive);
    }
    public boolean toggleShowInactive() {
        setBoolean(SHOW_INACTIVE, !getShowInactive());
        return getShowInactive();
    }
}
