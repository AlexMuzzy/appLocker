package uk.co.alexmusgrove.applocker.Helpers;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class appItem  implements Parcelable {
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

    public appItem(Parcel in) {
        this.mAppName = in.readString();
        this.mPackageName = in.readString();
        Bitmap bitmap = (Bitmap)in.readParcelable(getClass().getClassLoader());
        this.mLocked = in.readInt == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
