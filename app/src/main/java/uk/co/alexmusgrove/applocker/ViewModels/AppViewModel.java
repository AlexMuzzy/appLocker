package uk.co.alexmusgrove.applocker.ViewModels;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.ContentValues;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.co.alexmusgrove.applocker.Database.AppContentProvider;
import uk.co.alexmusgrove.applocker.Database.AppSQLiteDBHelper;
import uk.co.alexmusgrove.applocker.Helpers.appItem;

public class AppViewModel extends AndroidViewModel {

    private ArrayList<appItem> appItems;

    public AppViewModel(@NonNull Application application) {
        super(application);
    }

    private ArrayList<appItem> generateAppItems () {

        appItems = new ArrayList<>();

        final PackageManager pm = getApplication().getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            //filter all systems applications out of loop
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                //grab application label from package name
                String appName = (String) pm.getApplicationLabel(packageInfo);
                ArrayList<String> appList = AppSQLiteDBHelper.getAllApps(getApplication());
                //append details to appItem class
                if (!packageInfo.packageName.equals("uk.co.alexmusgrove.applocker")){
                    appItems.add(new appItem(
                            appName, //name of application
                            packageInfo.packageName, // unique package name of application
                            packageInfo.loadIcon(pm), // application icon
                            appList.contains(packageInfo.packageName) // app lock switch state
                    ));
                }
            }
        }


        Collections.sort(appItems, (object1, object2) ->
            object1.getmAppName().compareToIgnoreCase(object2.getmAppName()));
        return appItems;
    }


    public void addApp (appItem appItem, int position) {
        ContentValues values = new ContentValues();
        values.put(AppSQLiteDBHelper.COLUMN_PACKAGENAME, appItem.getmPackageName());
        getApplication().getContentResolver().insert(AppContentProvider.APP_CONTENT_URI, values);
        appItems.get(position).setmLocked(true);
    }

    public void removeApp (appItem appItem, int position) {
        getApplication().getContentResolver().delete(AppContentProvider.APP_CONTENT_URI, AppSQLiteDBHelper.COLUMN_PACKAGENAME + " = '" + appItem.getmPackageName() + "'", null);
        appItems.get(position).setmLocked(false);
    }

    public ArrayList<appItem> getAppItems() {
        return appItems;
    }
}
