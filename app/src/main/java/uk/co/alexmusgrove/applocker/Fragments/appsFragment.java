package uk.co.alexmusgrove.applocker.Fragments;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import uk.co.alexmusgrove.applocker.Adapters.appAdapter;
import uk.co.alexmusgrove.applocker.Helpers.appItem;
import uk.co.alexmusgrove.applocker.R;
import uk.co.alexmusgrove.applocker.ViewModels.AppViewModel;

public class appsFragment extends Fragment {

    AppViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_apps, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(AppViewModel.class);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buildRecyclerView();
    }

/*

    method and app list generation has moved to 'ViewModels/appViewModel'

    public void generateAppItems () {
        final PackageManager pm = getActivity().getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            //filter all systems applications out of loop
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                //grab application label from package name
                String appName = (String) pm.getApplicationLabel(packageInfo);
                ArrayList<String> appList = AppSQLiteDBHelper.getAllApps(getActivity());
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
        Collections.sort(appItems, (Comparator) (o1, o2) -> {
            appItem app1 = (appItem) o1;
            appItem app2 = (appItem) o2;
            return app1.getmAppName().compareToIgnoreCase(app2.getmAppName());
        });
    }
*/

    public void buildRecyclerView () {
        RecyclerView recyclerView = getView().findViewById(R.id.user_recycler_view);
        ArrayList<appItem> appItems = viewModel.getAppItems();
        recyclerView.setHasFixedSize(true);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        // use a linear layout manager
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        // specify an adapter
        appAdapter adapter = new appAdapter(viewModel.getAppItems());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(
                (position) -> launchAppIntent(appItems.get(position).getmPackageName())
        );
        adapter.setOnCheckedChangeListener((CompoundButton buttonView, int position, boolean isChecked) -> {
            if (buttonView.isShown()) {
                if (isChecked) {
                    viewModel.addApp(appItems.get(position), position);
                }
                if(!isChecked){
                    viewModel.removeApp(appItems.get(position), position);
                }
                Toast.makeText(
                        getActivity(),
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

        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        resolveInfos.sort(new ResolveInfo.DisplayNameComparator(pm));

        if (resolveInfos.size() > 0) {
            ResolveInfo launchable = resolveInfos.get(0);
            ActivityInfo activity = launchable.activityInfo;
            ComponentName name = new ComponentName(activity.applicationInfo.packageName,
                    activity.name);
            Intent i = new Intent(Intent.ACTION_MAIN);

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            i.setComponent(name);

            startActivity(i);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
