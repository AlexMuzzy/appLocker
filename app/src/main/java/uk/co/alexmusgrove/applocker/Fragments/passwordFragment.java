package uk.co.alexmusgrove.applocker.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import uk.co.alexmusgrove.applocker.Preferences.settingsPreferences;
import uk.co.alexmusgrove.applocker.R;

import static android.content.ContentValues.TAG;

public class passwordFragment extends DialogFragment {

    private static final String TAG = "uk.co.alexmusgrove.applocker.Fragments";

    public passwordFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Context context = getActivity();

        SharedPreferences prefs = settingsPreferences.getPrefs(context);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("New Password");

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.password_dialog_fragment, null);
        EditText editText = view.findViewById(R.id.resetpassword_editText);

        builder.setView(view);

        builder
                .setPositiveButton("Set Password", (dialog, id) -> {
                    settingsPreferences.setPassword(context, editText.getText().toString());
                })


                .setNegativeButton("Close App", (dialog, id) -> getActivity().finish());
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public static passwordFragment newInstance() {
        return new passwordFragment();
    }
}
