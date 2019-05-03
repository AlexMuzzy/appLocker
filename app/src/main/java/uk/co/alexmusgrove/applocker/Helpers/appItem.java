package uk.co.alexmusgrove.applocker.Helpers;

import android.graphics.drawable.Drawable;

public class appItem {
    private String mAppName;
    private String mPackageName;
    private Drawable mAppIcon;
    private boolean mLocked;

    //Constructor
    public appItem(String mAppName, String mPackageName, Drawable mAppIcon, boolean mLocked) {
        this.mAppName = mAppName;
        this.mPackageName = mPackageName;
        this.mAppIcon = mAppIcon;
        this.mLocked = mLocked;
    }

    //Getters and Setters
    public String getmAppName() {
        return mAppName;
    }

    public String getmPackageName() {
        return mPackageName;
    }

    public Drawable getmAppIcon() {
        return mAppIcon;
    }

    public boolean getmLocked() {
        return mLocked;
    }

    public void setmLocked(boolean mLocked) {
        this.mLocked = mLocked;
    }
}
