package uk.co.alexmusgrove.applocker.Helpers;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.co.alexmusgrove.applocker.Database.AppContentProvider;
import uk.co.alexmusgrove.applocker.Database.AppSQLiteDBHelper;

public class appItems {
    public ArrayList<appItem> appItems;
    private Context context;

    public appItems(Context context) {
        this.context = context;
        //static invocation of app items
        if (appItems == null) {
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
                if (!packageInfo.packageName.equals("uk.co.alexmusgrove.applocker")) {
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


    public void addApp(appItem appItem, int position) {
        ContentValues values = new ContentValues();
        values.put(AppSQLiteDBHelper.COLUMN_PACKAGENAME, appItem.getmPackageName());
        context.getContentResolver().insert(AppContentProvider.APP_CONTENT_URI, values);
        appItems.get(position).setmLocked(true);
    }

    public void removeApp(appItem appItem, int position) {
        context.getContentResolver().delete(
                AppContentProvider.APP_CONTENT_URI,
                AppSQLiteDBHelper.COLUMN_PACKAGENAME + " = '" + appItem.getmPackageName() + "'",
                null
        );
        appItems.get(position).setmLocked(false);
    }

    public void launchAppIntent(String packageName) {
        Intent intent = new Intent();
        intent.setPackage(packageName);

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentActivities(intent, 0);
        resolveInfo.sort(new ResolveInfo.DisplayNameComparator(pm));

        if (resolveInfo.size() > 0) {
            ResolveInfo launchable = resolveInfo.get(0);
            ActivityInfo activity = launchable.activityInfo;
            ComponentName name = new ComponentName(activity.applicationInfo.packageName,
                    activity.name);
            Intent i = new Intent(Intent.ACTION_MAIN);

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            i.setComponent(name);

            context.startActivity(i);
        }
    }
}
