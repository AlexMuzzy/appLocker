package uk.co.alexmusgrove.applocker.Activities;

import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import uk.co.alexmusgrove.applocker.Adapters.appAdapter;
import uk.co.alexmusgrove.applocker.Database.AppContentProvider;
import uk.co.alexmusgrove.applocker.Database.AppSQLiteDBHelper;
import uk.co.alexmusgrove.applocker.Fragments.permissionFragment;
import uk.co.alexmusgrove.applocker.R;
import uk.co.alexmusgrove.applocker.Helpers.appItem;
import uk.co.alexmusgrove.applocker.Services.appService;

public class  MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ArrayList<appItem> appItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!hasUsageStatsPermission()) {
            permissionFragment dialog = permissionFragment.newInstance();
            dialog.show(this.getSupportFragmentManager(), "permissionFragment");
        }
        generateAppItems();
        buildRecyclerView();
        startAppService();
    }

    //Overriding methods for options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        if(item.getItemId()==R.id.settings_item){
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void generateAppItems () {
        final PackageManager pm = getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            //filter all systems applications out of loop
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                //grab application label from package name
                String appName = (String) pm.getApplicationLabel(packageInfo);
                ArrayList<String> appList = AppSQLiteDBHelper.getAllApps(this);
                for (String app : appList) {
                    Log.i(TAG, "generateAppItems: " + app);
                }
                Log.i(TAG, "generateAppItems: " + appList.size());
                //append details to appItem class
                appItems.add(new appItem(
                        appName, //name of application
                        packageInfo.packageName, // unique package name of application
                        packageInfo.loadIcon(pm), // application icon
                        appList.contains(packageInfo.packageName) // app lock switch state
                ));
            }
        }
    }

    public void buildRecyclerView () {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        // specify an adapter
        appAdapter adapter = new appAdapter(appItems);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(
                (position) -> launchAppIntent(appItems.get(position).getmPackageName())
        );

        adapter.setOnCheckedChangeListener((CompoundButton buttonView, int position, boolean isChecked) -> {
            if (buttonView.isShown()) {
                if (isChecked) {
                    addApp(appItems.get(position), position);
                }
                Toast.makeText(
                        getApplicationContext(),
                        (isChecked)
                                ? "Locked " + appItems.get(position).getmAppName()
                                : "Unlocked " + appItems.get(position).getmAppName(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void launchAppIntent(String packageName) {
        Intent intent = new Intent();
        intent.setPackage(packageName);

        PackageManager pm = getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        resolveInfos.sort(new ResolveInfo.DisplayNameComparator(pm));

        if(resolveInfos.size() > 0) {
            ResolveInfo launchable = resolveInfos.get(0);
            ActivityInfo activity = launchable.activityInfo;
            ComponentName name=new ComponentName(activity.applicationInfo.packageName,
                    activity.name);
            Intent i=new Intent(Intent.ACTION_MAIN);

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            i.setComponent(name);

            startActivity(i);
        }
    }

    public void addApp (appItem appItem, int position) {
        ContentValues values = new ContentValues();
        values.put(AppSQLiteDBHelper.COLUMN_PACKAGENAME, appItem.getmPackageName());
        getContentResolver().insert(AppContentProvider.CONTENT_URI, values);
        appItems.get(position).setmLocked(true);
    }


    public void startAppService () {
        if (hasUsageStatsPermission()) {
            //check if user has granted permission for usage of this service.
            Intent appService = new Intent(this, appService.class);
            appService.setAction(USAGE_STATS_SERVICE);
            stopService(appService);
            startService(appService);
        }
    }

    private boolean hasUsageStatsPermission () {
        AppOpsManager appOpsManager = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getApplicationContext().getPackageName());
        return (mode == AppOpsManager.MODE_ALLOWED);
    }
}

