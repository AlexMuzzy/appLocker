package uk.co.alexmusgrove.applocker.Helpers;

import android.graphics.drawable.Drawable;

public class appItem {
    private String mText1;
    private String mPackageName;
    private Drawable mAppIcon;

    //Constructor
    public appItem(String mText1, String mPackageName, Drawable mAppIcon) {
        this.mText1 = mText1;
        this.mPackageName = mPackageName;
        this.mAppIcon = mAppIcon;
    }

    //Getters and Setters
    public String getmText1() {
        return mText1;
    }


    public String getmPackageName() {
        return mPackageName;
    }

    public Drawable getmAppIcon() {
        return mAppIcon;
    }
}
