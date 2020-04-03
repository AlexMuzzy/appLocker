package uk.co.alexmusgrove.applocker.Helpers;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.co.alexmusgrove.applocker.Database.AppSQLiteDBHelper;

public class appItems {
    public ArrayList<appItem> appItems;
    static Context context;

    public appItems(Context context) {
        this.context = context;
        if (appItems == null){
            appItems = new ArrayList<>();
            generateAppItems();
        }
    }

    private void generateAppItems() {
        final PackageManager pm = context.getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            //filter all systems applications out of loop
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                //grab application label from package name
                String appName = (String) pm.getApplicationLabel(packageInfo);
                ArrayList<String> appList = AppSQLiteDBHelper.getAllApps(context);
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
        Collections.sort(appItems, (app1, app2) -> app1.getmAppName().compareToIgnoreCase(app2.getmAppName()));
    }
}
