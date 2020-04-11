package uk.co.alexmusgrove.applocker.Fragments;

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
import java.util.Objects;

import uk.co.alexmusgrove.applocker.Activities.HomeActivity;
import uk.co.alexmusgrove.applocker.Adapters.appAdapter;
import uk.co.alexmusgrove.applocker.Helpers.appItem;
import uk.co.alexmusgrove.applocker.Helpers.appItems;
import uk.co.alexmusgrove.applocker.R;

public class appsFragment extends Fragment {

    private ArrayList<appItem> appItemArrayList;
    private appItems appItems;
    private boolean lockFragmentState;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_apps, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appItemArrayList = ((HomeActivity) Objects.requireNonNull(getActivity())).getAppItems();
        appItems = new appItems(getActivity());
        buildRecyclerView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void buildRecyclerView () {
        RecyclerView recyclerView = getView().findViewById(R.id.user_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        // specify an adapter
        appAdapter adapter = new appAdapter(appItemArrayList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(
                (position) -> appItems.launchAppIntent(appItemArrayList.get(position).getmPackageName())
        );
        adapter.setOnCheckedChangeListener((CompoundButton buttonView, int position, boolean isChecked) -> {
            if (buttonView.isShown()) {
                if (isChecked) {
                    appItems.addApp(appItemArrayList.get(position), position);
                }
                if(!isChecked){
                    appItems.removeApp(appItemArrayList.get(position), position);
                }
                Toast.makeText(
                        getActivity(),
                        (isChecked)
                                ? "Locked " + appItemArrayList.get(position).getmAppName()
                                : "Unlocked " + appItemArrayList.get(position).getmAppName(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
