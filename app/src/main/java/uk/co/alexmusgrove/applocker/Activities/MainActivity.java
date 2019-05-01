package uk.co.alexmusgrove.applocker.Activities;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.alexmusgrove.applocker.Adapters.appAdapter;
import uk.co.alexmusgrove.applocker.Database.AppContentProvider;
import uk.co.alexmusgrove.applocker.Database.AppSQLiteDBHelper;
import uk.co.alexmusgrove.applocker.R;
import uk.co.alexmusgrove.applocker.Helpers.appItem;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ArrayList<appItem> appItems = new ArrayList<>();
    private ContentResolver myCR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        generateAppItems();
        buildRecyclerView();
        //getAllApps();
        checkDatabase();
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

                //append details to appItem class
                appItems.add(new appItem(
                        appName, //name of application
                        packageInfo.packageName, // unique package name of application
                        packageInfo.loadIcon(pm), // application icon
                        false // app lock switch state
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

        adapter.setOnCheckedChangeListener((int position, boolean isChecked) -> {
            //TODO
                if (isChecked){
                    addApp(appItems.get(position));
                }
                Toast.makeText(
                        getApplicationContext(),
                        (isChecked)
                                ? "Locked "  + appItems.get(position).getmAppName()
                                : "Unlocked " + appItems.get(position).getmAppName(),
                        Toast.LENGTH_SHORT).show();

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

    public void addApp (appItem appItem) {
        ContentValues values = new ContentValues();
        values.put(AppSQLiteDBHelper.COLUMN_PACKAGENAME, appItem.getmPackageName());
        myCR.insert(AppContentProvider.CONTENT_URI, values);
    }

/*    public ArrayList<String> getAllApps () {
        ArrayList<String> apps = new ArrayList<>();
        Cursor cursor = myCR.query(AppContentProvider.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()){
            apps.add(cursor.getString(1));
        }
        return apps;
    }*/

    public void checkDatabase () {
        File database=getApplicationContext().getDatabasePath(AppSQLiteDBHelper.DATABASE_NAME);

        if (!database.exists()) {
            // Database does not exist so copy it from assets here
            Log.i("Database", "Not Found");
            Context context = this;
            String dbpath = context.getDatabasePath(AppSQLiteDBHelper.DATABASE_NAME).getPath();

        } else {
            Log.i("Database", "Found");
        }
    }
}
