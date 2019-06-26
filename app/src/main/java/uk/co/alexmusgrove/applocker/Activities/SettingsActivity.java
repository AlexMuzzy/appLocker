package uk.co.alexmusgrove.applocker.Activities;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import uk.co.alexmusgrove.applocker.Fragments.passwordFragment;
import uk.co.alexmusgrove.applocker.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Settings");
        setContentView(R.layout.activity_settings);
        CardView resetpassword_card = this.findViewById(R.id.resetpassword_view);
        resetpassword_card.setOnClickListener(v -> {
            passwordFragment dialog = passwordFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            dialog.show(fragmentManager,"passwordFragment");
        });
    }
}
