package uk.co.alexmusgrove.applocker.Activities;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import uk.co.alexmusgrove.applocker.Fragments.passwordFragment;
import uk.co.alexmusgrove.applocker.Fragments.permissionFragment;
import uk.co.alexmusgrove.applocker.Fragments.settingsFragment;
import uk.co.alexmusgrove.applocker.Fragments.appsFragment;
import uk.co.alexmusgrove.applocker.Helpers.appItem;
import uk.co.alexmusgrove.applocker.Helpers.appItems;
import uk.co.alexmusgrove.applocker.Preferences.settingsPreferences;
import uk.co.alexmusgrove.applocker.R;

public class HomeActivity extends AppCompatActivity {

    public appItems appItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (!hasUsageStatsPermission()) {
            permissionFragment dialog = permissionFragment.newInstance();
            dialog.show(this.getSupportFragmentManager(), "permissionFragment");
        }

        generateBottomNavBar();

        if (!hasPasswordSet()) {
            passwordFragment dialog = passwordFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            dialog.show(fragmentManager, "passwordFragment");
        }

        appItems = new appItems(getApplicationContext());

        loadFragment(new appsFragment());

        startAppService();

    }

    public void generateBottomNavBar() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            Fragment fragment = null;
            Bundle bundle = new Bundle();

            switch (item.getItemId()) {
                case R.id.action_apps:
                    bundle.putBoolean("lockFragment", false);
                    Toast.makeText(HomeActivity.this, R.string.user_apps, Toast.LENGTH_SHORT).show();

                    fragment = new appsFragment();
                    fragment.setArguments(bundle);
                    break;

                case R.id.action_locked:
                    bundle.putBoolean("lockFragment", true);
                    Toast.makeText(HomeActivity.this, R.string.locked_apps, Toast.LENGTH_SHORT).show();

                    fragment = new appsFragment();
                    fragment.setArguments(bundle);
                    break;

                case R.id.action_settings:
                    Toast.makeText(HomeActivity.this, R.string.settings, Toast.LENGTH_SHORT).show();
                    fragment = new settingsFragment();
                    break;
            }

            return loadFragment(fragment);
        });
    }


    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }
        return false;
    }


    private boolean hasUsageStatsPermission() {
        AppOpsManager appOpsManager = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (appOpsManager != null) {
            mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getApplicationContext().getPackageName());
        }
        return (mode == AppOpsManager.MODE_ALLOWED);
    }

    public void startAppService() {
        if (hasUsageStatsPermission()) {
            //check if user has granted permission for usage of this service.
            Intent appService = new Intent(this, uk.co.alexmusgrove.applocker.Services.appService.class);
            appService.setAction(USAGE_STATS_SERVICE);
            startService(appService);
        }
    }

    private boolean hasPasswordSet() {
        return !(settingsPreferences.getPassword(this) == null);
    }

    public ArrayList<appItem> getAppItems() {
        return appItems.appItems;
    }
}

