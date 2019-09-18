package uk.co.alexmusgrove.applocker.Activities;

import android.app.AppOpsManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uk.co.alexmusgrove.applocker.Database.AppContentProvider;
import uk.co.alexmusgrove.applocker.Database.AppSQLiteDBHelper;
import uk.co.alexmusgrove.applocker.Fragments.passwordFragment;
import uk.co.alexmusgrove.applocker.Fragments.permissionFragment;
import uk.co.alexmusgrove.applocker.Fragments.settingsFragment;
import uk.co.alexmusgrove.applocker.Fragments.appsFragment;
import uk.co.alexmusgrove.applocker.Helpers.appItem;
import uk.co.alexmusgrove.applocker.Preferences.settingsPreferences;
import uk.co.alexmusgrove.applocker.R;

public class HomeActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (!hasUsageStatsPermission()) {
            permissionFragment dialog = permissionFragment.newInstance();
            dialog.show(this.getSupportFragmentManager(), "permissionFragment");
        }

        generateBottomNavBar();

        if(!hasPasswordSet()){
            passwordFragment dialog = passwordFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            dialog.show(fragmentManager,"passwordFragment");
        }

        loadFragment(new appsFragment());

        startAppService();
    }

    public void generateBottomNavBar () {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.action_user:
                    Toast.makeText(HomeActivity.this, "User Apps", Toast.LENGTH_SHORT).show();
                    fragment = new appsFragment();
                    break;

                case R.id.action_system:
                    Toast.makeText(HomeActivity.this, "System Apps", Toast.LENGTH_SHORT).show();
                    fragment = new appsFragment();
                    break;

                case R.id.action_settings:
                    Toast.makeText(HomeActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                    fragment = new settingsFragment();
                    break;
            }

            return loadFragment(fragment);
        });
    }


    private boolean loadFragment(Fragment fragment){
        if (fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }
        return false;
    }


    private boolean hasUsageStatsPermission () {
        AppOpsManager appOpsManager = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (appOpsManager != null) {
            mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getApplicationContext().getPackageName());
        }
        return (mode == AppOpsManager.MODE_ALLOWED);
    }

    public void startAppService () {
        if (hasUsageStatsPermission()) {
            //check if user has granted permission for usage of this service.
            Intent appService = new Intent(this, uk.co.alexmusgrove.applocker.Services.appService.class);
            appService.setAction(USAGE_STATS_SERVICE);
            startService(appService);
        }
    }

    private boolean hasPasswordSet () {
        return !(settingsPreferences.getPassword(this) == null);
    }
}
