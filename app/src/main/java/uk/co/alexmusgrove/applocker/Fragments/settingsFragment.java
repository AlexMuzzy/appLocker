package uk.co.alexmusgrove.applocker.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.alexmusgrove.applocker.R;

public class settingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CardView resetpassword_card = getActivity().findViewById(R.id.resetpassword_view);
        resetpassword_card.setOnClickListener(v -> {
            passwordFragment dialog = passwordFragment.newInstance();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            dialog.show(fragmentManager,"passwordFragment");
        });
    }


}
